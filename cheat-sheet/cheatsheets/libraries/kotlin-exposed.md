# Exposed

Exposed - это легковесная SQL библиотека для Kotlin, предоставляющая DSL для работы с базами данных. Поддерживает различные СУБД и предлагает type-safe SQL queries.

**Дата последнего обновления:** 2026-01-25

## Полезные ссылки

### Официальная документация
- [Exposed](https://github.com/JetBrains/Exposed) - GitHub репозиторий
- [Exposed Wiki](https://github.com/JetBrains/Exposed/wiki) - Wiki документация
- [Exposed Documentation](https://github.com/JetBrains/Exposed/wiki) - Документация

### См. также
- `../libraries/java-hikaricp.md` - HikariCP для connection pooling
- `../libraries/java-jooq.md` - jOOQ для type-safe SQL
- `../libraries/scala-doobie.md` - Doobie для функционального программирования с БД

## Содержание

- [Основные возможности](#основные-возможности)
  - [Database Connection](#database-connection)
  - [Table Definition](#table-definition)
  - [Schema Operations](#schema-operations)
- [CRUD операции](#crud-операции)
  - [Insert Operations](#insert-operations)
  - [Select Operations](#select-operations)
  - [Update Operations](#update-operations)
  - [Delete Operations](#delete-operations)
- [Продвинутые запросы](#продвинутые-запросы)
  - [Complex Queries](#complex-queries)
  - [Custom SQL](#custom-sql)
- [Транзакции](#транзакции)
  - [Transactions](#transactions)
- [DAO Pattern](#dao-pattern)
  - [DAO Implementation](#dao-implementation)
  - [Relationships](#relationships)
- [Spring Boot Integration](#spring-boot-integration)
  - [Configuration](#configuration)
  - [Repository Layer](#repository-layer)
  - [Service Layer](#service-layer)
- [Testing](#testing)
  - [Unit Testing с Exposed](#unit-testing-с-exposed)
  - [Integration Testing](#integration-testing)
- [Best Practices](#best-practices)
  - [Schema Design](#schema-design)
  - [Query Optimization](#query-optimization)
  - [Error Handling](#error-handling)
- [Schema Migrations](#schema-migrations)
  - [Schema Migrations](#schema-migrations-1)
- [Performance Optimization](#performance-optimization)
  - [Connection Pooling](#connection-pooling)
  - [Query Optimization](#query-optimization-1)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Debugging Queries](#debugging-queries)
- [Migration Guide](#migration-guide)
  - [From JDBC to Exposed](#from-jdbc-to-exposed)
  - [From Hibernate to Exposed](#from-hibernate-to-exposed)
- [Experimental Features](#experimental-features)
  - [Exposed 0.40+ Features (Future)](#exposed-040-features-future)

## Основные возможности

### Database Connection
```kotlin
import org.jetbrains.exposed.sql.Database
import com.zaxxer.hikari.HikariDataSource

/**
 * Подключение к базе данных в Exposed
 * Exposed поддерживает различные СУБД через JDBC драйверы
 */
// Подключение к H2 (in-memory база данных для тестирования)
// H2 - легковесная БД, которая хранит данные в памяти
val db = Database.connect(
    url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",  // URL для in-memory H2
    // mem:test - имя базы данных в памяти
    // DB_CLOSE_DELAY=-1 - база не закрывается при отключении последнего соединения
    driver = "org.h2.Driver"  // JDBC драйвер для H2
)

// Подключение к PostgreSQL
// PostgreSQL - мощная реляционная СУБД
val postgresDb = Database.connect(
    url = "jdbc:postgresql://localhost:5432/mydb",  // URL PostgreSQL сервера
    // localhost:5432 - адрес и порт PostgreSQL
    // mydb - имя базы данных
    driver = "org.postgresql.Driver",  // JDBC драйвер для PostgreSQL
    user = "username",  // Имя пользователя для подключения
    password = "password"  // Пароль для подключения
)

// Подключение с DataSource (рекомендуется для production)
// DataSource предоставляет connection pooling и лучшую производительность
val dataSource = HikariDataSource().apply {
    // HikariCP - быстрый connection pool
    jdbcUrl = "jdbc:mysql://localhost:3306/mydb"  // URL MySQL сервера
    username = "user"  // Имя пользователя
    password = "pass"  // Пароль
    driverClassName = "com.mysql.cj.jdbc.Driver"  // JDBC драйвер для MySQL
    // HikariCP автоматически управляет пулом соединений
}

// Подключение Exposed к DataSource
val mysqlDb = Database.connect(dataSource)
// Exposed использует существующий DataSource для получения соединений
// Это позволяет использовать connection pooling и другие оптимизации
```

### Table Definition
```kotlin
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

/**
 * Определение таблиц в Exposed
 * Таблицы определяются как object, наследующие Table
 * Колонки определяются как свойства с типами данных
 */
// Определение таблицы пользователей
// object Users - singleton объект, представляющий таблицу
// Table("users") - имя таблицы в базе данных
object Users : Table("users") {
    // integer("id") - целочисленная колонка с именем "id"
    // autoIncrement() - автоматическое увеличение значения (AUTO_INCREMENT/SERIAL)
    val id = integer("id").autoIncrement()
    
    // varchar("name", 50) - строковая колонка с максимальной длиной 50 символов
    val name = varchar("name", 50)
    
    // uniqueIndex() - создание уникального индекса на колонку
    // Гарантирует уникальность значений email
    val email = varchar("email", 100).uniqueIndex()
    
    // nullable() - разрешает NULL значения в колонке
    val age = integer("age").nullable()
    
    // datetime() - колонка типа DATETIME
    // defaultExpression(CurrentDateTime()) - значение по умолчанию (текущая дата/время)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())

    // override val primaryKey - определение первичного ключа
    // PrimaryKey(id) - первичный ключ на колонке id
    override val primaryKey = PrimaryKey(id)
}

// Таблица с внешними ключами
// Posts связана с Users через внешний ключ
object Posts : Table("posts") {
    val id = integer("id").autoIncrement()
    
    // references(Users.id) - внешний ключ на Users.id
    // onDelete = ReferenceOption.CASCADE - каскадное удаление
    // При удалении пользователя удаляются все его посты
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    
    val title = varchar("title", 200)  // Заголовок поста (до 200 символов)
    val content = text("content")  // text() - колонка типа TEXT (для больших текстов)
    
    // bool() - булева колонка
    // default(false) - значение по умолчанию false
    val published = bool("published").default(false)

    override val primaryKey = PrimaryKey(id)
}

// Many-to-many relationship (связь многие-ко-многим)
// Связующая таблица для связи Users и Groups
object UserGroups : Table("user_groups") {
    // Внешний ключ на Users
    val userId = integer("user_id").references(Users.id)
    // Внешний ключ на Groups
    val groupId = integer("group_id").references(Groups.id)

    // Составной первичный ключ (composite primary key)
    // Первичный ключ состоит из двух колонок
    override val primaryKey = PrimaryKey(userId, groupId)
}

// Таблица групп
object Groups : Table("groups") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}
```

### Schema Operations
```kotlin
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun createSchema() {
    transaction {
        // Создание таблиц
        SchemaUtils.create(Users, Posts, Groups, UserGroups)

        // Создание индексов
        SchemaUtils.createMissingTablesAndColumns(Users, Posts)
    }
}

fun dropSchema() {
    transaction {
        // Удаление таблиц
        SchemaUtils.drop(Users, Posts, Groups, UserGroups)
    }
}

fun updateSchema() {
    transaction {
        // Добавление недостающих колонок и таблиц
        SchemaUtils.createMissingTablesAndColumns(Users, Posts)

        // Или миграция с кастомной логикой
        if (!Users.age.exists()) {
            exec("ALTER TABLE users ADD COLUMN age INT")
        }
    }
}
```

## CRUD Operations

### Insert Operations
```kotlin
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId

fun insertUser(name: String, email: String, age: Int? = null) {
    transaction {
        val userId = Users.insertAndGetId {
            it[Users.name] = name
            it[Users.email] = email
            if (age != null) {
                it[Users.age] = age
            }
        }

        println("Inserted user with ID: $userId")
    }
}

fun insertMultipleUsers() {
    transaction {
        val users = listOf(
            Triple("Alice", "alice@example.com", 25),
            Triple("Bob", "bob@example.com", 30),
            Triple("Charlie", "charlie@example.com", 35)
        )

        Users.batchInsert(users) { (name, email, age) ->
            this[Users.name] = name
            this[Users.email] = email
            this[Users.age] = age
        }
    }
}

fun insertPost(userId: Int, title: String, content: String) {
    transaction {
        Posts.insert {
            it[Posts.userId] = userId
            it[Posts.title] = title
            it[Posts.content] = content
        }
    }
}
```

### Select Operations
```kotlin
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

// Простой select всех пользователей
fun getAllUsers(): List<User> {
    return transaction {
        Users.selectAll().map {
            User(
                id = it[Users.id],
                name = it[Users.name],
                email = it[Users.email],
                age = it[Users.age]
            )
        }
    }
}

// Select с условиями
fun getUserById(userId: Int): User? {
    return transaction {
        Users.select { Users.id eq userId }
            .singleOrNull()
            ?.let {
                User(
                    id = it[Users.id],
                    name = it[Users.name],
                    email = it[Users.email],
                    age = it[Users.age]
                )
            }
    }
}

fun getUsersByAgeRange(minAge: Int, maxAge: Int): List<User> {
    return transaction {
        Users.select {
            (Users.age greaterEq minAge) and (Users.age lessEq maxAge)
        }.map {
            User(
                id = it[Users.id],
                name = it[Users.name],
                email = it[Users.email],
                age = it[Users.age]
            )
        }
    }
}

// Joins
fun getPostsWithUsers(): List<PostWithUser> {
    return transaction {
        (Posts innerJoin Users)
            .selectAll()
            .map {
                PostWithUser(
                    postId = it[Posts.id],
                    postTitle = it[Posts.title],
                    postContent = it[Posts.content],
                    userName = it[Users.name],
                    userEmail = it[Users.email]
                )
            }
    }
}
```

### Update Operations
```kotlin
import org.jetbrains.exposed.sql.update

fun updateUser(userId: Int, newName: String, newEmail: String) {
    transaction {
        Users.update({ Users.id eq userId }) {
            it[Users.name] = newName
            it[Users.email] = newEmail
        }
    }
}

fun incrementUserAge(userId: Int) {
    transaction {
        Users.update({ Users.id eq userId }) {
            with(SqlExpressionBuilder) {
                it[Users.age] = Users.age + 1
            }
        }
    }
}

fun updateMultipleUsers() {
    transaction {
        Users.update({ Users.age less 18 }) {
            it[Users.age] = 18
        }
    }
}
```

### Delete Operations
```kotlin
import org.jetbrains.exposed.sql.deleteWhere

fun deleteUser(userId: Int) {
    transaction {
        Users.deleteWhere { Users.id eq userId }
    }
}

fun deleteUsersByAge(maxAge: Int) {
    transaction {
        Users.deleteWhere { Users.age greater maxAge }
    }
}

fun deleteOldPosts() {
    transaction {
        val oneMonthAgo = Clock.System.now()
            .minus(30, DateTimeUnit.DAY)
            .toLocalDateTime(TimeZone.currentSystemDefault())

        Posts.deleteWhere {
            Posts.createdAt less oneMonthAgo
        }
    }
}
```

## Продвинутые возможности

### Complex Queries
```kotlin
import org.jetbrains.exposed.sql.*

// Агрегационные запросы
fun getUserStatistics(): UserStats {
    return transaction {
        val totalUsers = Users.selectAll().count()

        val avgAge = Users.select(Users.age.avg()).singleOrNull()?.get(Users.age.avg())

        val ageGroups = Users.select(Users.age, Users.age.count())
            .groupBy(Users.age)
            .orderBy(Users.age)
            .map { it[Users.age] to it[Users.age.count()].toInt() }

        UserStats(totalUsers, avgAge, ageGroups)
    }
}

// Subqueries
fun getUsersWithPosts(): List<UserWithPostCount> {
    return transaction {
        val postCount = Posts.userId.count()

        Users.leftJoin(Posts)
            .select(Users.id, Users.name, Users.email, postCount)
            .groupBy(Users.id, Users.name, Users.email)
            .map {
                UserWithPostCount(
                    id = it[Users.id],
                    name = it[Users.name],
                    email = it[Users.email],
                    postCount = it[postCount].toInt()
                )
            }
    }
}

// Window functions
fun getUsersWithRank(): List<UserWithRank> {
    return transaction {
        val rank = Users.age.rank().over(orderBy = Users.age.desc())

        Users.select(Users.id, Users.name, Users.age, rank)
            .orderBy(Users.age, SortOrder.DESC)
            .map {
                UserWithRank(
                    id = it[Users.id],
                    name = it[Users.name],
                    age = it[Users.age],
                    rank = it[rank].toInt()
                )
            }
    }
}
```

### Custom SQL
```kotlin
import org.jetbrains.exposed.sql.SqlExpressionBuilder

// Raw SQL queries
fun executeCustomQuery(): List<Map<String, Any?>> {
    return transaction {
        exec("SELECT name, COUNT(*) as post_count FROM users u LEFT JOIN posts p ON u.id = p.user_id GROUP BY u.id, u.name") { rs ->
            val results = mutableListOf<Map<String, Any?>>()
            while (rs.next()) {
                results.add(mapOf(
                    "name" to rs.getString("name"),
                    "post_count" to rs.getInt("post_count")
                ))
            }
            results
        } ?: emptyList()
    }
}

// Stored procedures
fun callStoredProcedure(userId: Int): String {
    return transaction {
        val result = exec("CALL get_user_status(?)") { rs ->
            if (rs.next()) rs.getString("status") else "unknown"
        }
        result ?: "unknown"
    }
}

// Dynamic SQL
fun buildDynamicQuery(filters: Map<String, Any>): Query {
    val query = Users.selectAll()

    filters.forEach { (field, value) ->
        when (field) {
            "name" -> query.andWhere { Users.name eq value as String }
            "email" -> query.andWhere { Users.email eq value as String }
            "minAge" -> query.andWhere { Users.age greaterEq value as Int }
            "maxAge" -> query.andWhere { Users.age lessEq value as Int }
        }
    }

    return query
}
```

### Transactions
```kotlin
import org.jetbrains.exposed.sql.transactions.experimental.*

// Spring-style declarative transactions
suspend fun createUserWithPosts(userName: String, posts: List<String>) {
    newSuspendedTransaction {
        // Все операции в одной транзакции
        val userId = Users.insertAndGetId {
            it[Users.name] = userName
            it[Users.email] = "$userName@example.com"
        }

        Posts.batchInsert(posts) { postTitle ->
            this[Posts.userId] = userId.value
            this[Posts.title] = postTitle
            this[Posts.content] = "Content for $postTitle"
        }
    }
}

// Manual transaction management
fun transferPoints(fromUserId: Int, toUserId: Int, points: Int) {
    transaction {
        // Проверка баланса
        val fromUser = Users.select { Users.id eq fromUserId }
            .singleOrNull() ?: throw IllegalArgumentException("User not found")

        val currentPoints = fromUser[Users.points] ?: 0
        if (currentPoints < points) {
            throw IllegalArgumentException("Insufficient points")
        }

        // Перевод средств
        Users.update({ Users.id eq fromUserId }) {
            with(SqlExpressionBuilder) {
                it[Users.points] = Users.points - points
            }
        }

        Users.update({ Users.id eq toUserId }) {
            with(SqlExpressionBuilder) {
                it[Users.points] = Users.points + points
            }
        }
    }
}

// Nested transactions
fun complexOperation() {
    transaction {
        // Outer transaction
        Users.insert { /* ... */ }

        transaction {
            // Nested transaction
            Posts.insert { /* ... */ }
            // Если здесь ошибка, только nested transaction откатится
        }

        Groups.insert { /* ... */ }
    }
}
```

## DAO Pattern

### DAO Implementation
```kotlin
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

// Table definition
object UsersTable : IntIdTable("users") {
    val name = varchar("name", 50)
    val email = varchar("email", 100).uniqueIndex()
    val age = integer("age").nullable()
}

// Entity class
class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UsersTable)

    var name by UsersTable.name
    var email by UsersTable.email
    var age by UsersTable.age

    val posts by PostEntity referrersOn PostsTable.userId
}

// DAO operations
object UserDao {
    fun create(name: String, email: String, age: Int?): UserEntity {
        return UserEntity.new {
            this.name = name
            this.email = email
            this.age = age
        }
    }

    fun findById(id: Int): UserEntity? {
        return UserEntity.findById(id)
    }

    fun findByEmail(email: String): UserEntity? {
        return UserEntity.find { UsersTable.email eq email }.singleOrNull()
    }

    fun findAll(): List<UserEntity> {
        return UserEntity.all().toList()
    }

    fun update(id: Int, name: String, email: String): UserEntity? {
        return UserEntity.findById(id)?.apply {
            this.name = name
            this.email = email
        }
    }

    fun delete(id: Int): Boolean {
        return UserEntity.findById(id)?.delete() ?: false
    }
}
```

### Relationships
```kotlin
object PostsTable : IntIdTable("posts") {
    val userId = reference("user_id", UsersTable)
    val title = varchar("title", 200)
    val content = text("content")
}

class PostEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PostEntity>(PostsTable)

    var userId by PostsTable.userId
    var title by PostsTable.title
    var content by PostsTable.content

    val user by UserEntity referencedOn PostsTable.userId
}

// Работа с отношениями
fun createPostForUser(userId: Int, title: String, content: String): PostEntity {
    return PostEntity.new {
        this.userId = EntityID(userId, UsersTable)
        this.title = title
        this.content = content
    }
}

fun getUserWithPosts(userId: Int): UserWithPosts? {
    return UserEntity.findById(userId)?.let { user ->
        UserWithPosts(
            user = user,
            posts = user.posts.toList()
        )
    }
}
```

## Integration с Spring Boot

### Configuration
```kotlin
@Configuration
class DatabaseConfig {

    @Bean
    fun dataSource(): HikariDataSource {
        return HikariDataSource().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/mydb"
            username = "user"
            password = "password"
            driverClassName = "org.postgresql.Driver"

            maximumPoolSize = 10
            minimumIdle = 5
            idleTimeout = 300000
        }
    }

    @Bean
    fun database(dataSource: HikariDataSource): Database {
        return Database.connect(dataSource).also {
            // Инициализация схемы
            transaction {
                SchemaUtils.createMissingTablesAndColumns(
                    UsersTable, PostsTable, GroupsTable
                )
            }
        }
    }
}
```

### Repository Layer
```kotlin
@Repository
class UserRepository(private val database: Database) {

    suspend fun findById(id: Long): User? = newSuspendedTransaction(db = database) {
        UserEntity.findById(id.toInt())?.toUser()
    }

    suspend fun findByEmail(email: String): User? = newSuspendedTransaction(db = database) {
        UserEntity.find { UsersTable.email eq email }
            .singleOrNull()
            ?.toUser()
    }

    suspend fun save(user: User): User = newSuspendedTransaction(db = database) {
        val entity = if (user.id == 0L) {
            UserEntity.new {
                name = user.name
                email = user.email
                age = user.age
            }
        } else {
            UserEntity.findById(user.id.toInt())?.apply {
                name = user.name
                email = user.email
                age = user.age
            } ?: throw IllegalArgumentException("User not found")
        }
        entity.toUser()
    }

    suspend fun delete(id: Long): Boolean = newSuspendedTransaction(db = database) {
        UserEntity.findById(id.toInt())?.delete() ?: false
    }

    suspend fun findAll(): List<User> = newSuspendedTransaction(db = database) {
        UserEntity.all().map { it.toUser() }
    }
}

// Extension functions
fun UserEntity.toUser() = User(
    id = id.value.toLong(),
    name = name,
    email = email,
    age = age
)
```

### Service Layer
```kotlin
@Service
class UserService(
    private val userRepository: UserRepository,
    private val emailService: EmailService
) {

    suspend fun createUser(request: CreateUserRequest): User {
        // Валидация
        validateUserRequest(request)

        // Проверка уникальности email
        val existingUser = userRepository.findByEmail(request.email)
        if (existingUser != null) {
            throw EmailAlreadyExistsException()
        }

        // Создание пользователя
        val user = User(
            id = 0,
            name = request.name,
            email = request.email,
            age = request.age
        )

        val savedUser = userRepository.save(user)

        // Отправка email (асинхронно)
        launch {
            try {
                emailService.sendWelcomeEmail(savedUser.email)
            } catch (e: Exception) {
                // Логирование ошибки
            }
        }

        return savedUser
    }

    suspend fun getUser(id: Long): User {
        return userRepository.findById(id)
            ?: throw UserNotFoundException()
    }

    suspend fun updateUser(id: Long, request: UpdateUserRequest): User {
        val existingUser = getUser(id)

        val updatedUser = existingUser.copy(
            name = request.name ?: existingUser.name,
            email = request.email ?: existingUser.email,
            age = request.age ?: existingUser.age
        )

        return userRepository.save(updatedUser)
    }

    suspend fun deleteUser(id: Long) {
        val deleted = userRepository.delete(id)
        if (!deleted) {
            throw UserNotFoundException()
        }
    }

    private fun validateUserRequest(request: CreateUserRequest) {
        require(request.name.isNotBlank()) { "Name cannot be blank" }
        require(request.email.isNotBlank()) { "Email cannot be blank" }
        require(request.email.contains("@")) { "Invalid email format" }
        require(request.age == null || request.age >= 18) { "Age must be at least 18" }
    }
}
```

## Testing

### Unit Testing с Exposed
```kotlin
class UserRepositoryTest {

    private lateinit var database: Database
    private lateinit var userRepository: UserRepository

    @BeforeEach
    fun setUp() {
        // In-memory H2 database for testing
        database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )

        userRepository = UserRepository(database)

        // Create schema
        transaction(db = database) {
            SchemaUtils.create(UsersTable, PostsTable)
        }
    }

    @AfterEach
    fun tearDown() {
        transaction(db = database) {
            SchemaUtils.drop(UsersTable, PostsTable)
        }
    }

    @Test
    fun `should save and retrieve user`() = runTest {
        val user = User(id = 0, name = "John", email = "john@example.com", age = 30)

        val savedUser = userRepository.save(user)
        assertNotNull(savedUser.id)

        val retrievedUser = userRepository.findById(savedUser.id)
        assertNotNull(retrievedUser)
        assertEquals("John", retrievedUser?.name)
        assertEquals("john@example.com", retrievedUser?.email)
    }

    @Test
    fun `should return null for non-existent user`() = runTest {
        val user = userRepository.findById(999)
        assertNull(user)
    }

    @Test
    fun `should update user correctly`() = runTest {
        val user = User(id = 0, name = "John", email = "john@example.com", age = 30)
        val savedUser = userRepository.save(user)

        val updatedUser = savedUser.copy(name = "John Doe", age = 31)
        val result = userRepository.save(updatedUser)

        assertEquals("John Doe", result.name)
        assertEquals(31, result.age)
    }
}
```

### Integration Testing
```kotlin
@SpringBootTest
@Testcontainers
class UserServiceIntegrationTest {

    @Container
    private val postgres = PostgreSQLContainer<Nothing>("postgres:13").apply {
        withDatabaseName("testdb")
        withUsername("test")
        withPassword("test")
    }

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should create user successfully`() = runTest {
        val request = CreateUserRequest(
            name = "John Doe",
            email = "john@example.com",
            age = 30
        )

        val user = userService.createUser(request)

        assertNotNull(user.id)
        assertEquals("John Doe", user.name)
        assertEquals("john@example.com", user.email)

        // Verify in database
        val fromDb = userRepository.findById(user.id)
        assertNotNull(fromDb)
        assertEquals(user, fromDb)
    }

    @Test
    fun `should throw exception for duplicate email`() = runTest {
        val request1 = CreateUserRequest("John", "john@example.com", 30)
        val request2 = CreateUserRequest("Jane", "john@example.com", 25)

        userService.createUser(request1)

        assertThrows<EmailAlreadyExistsException> {
            userService.createUser(request2)
        }
    }

    @Test
    fun `should handle concurrent operations correctly`() = runTest {
        val requests = List(10) { index ->
            CreateUserRequest(
                name = "User$index",
                email = "user$index@example.com",
                age = 20 + index
            )
        }

        // Create users concurrently
        val users = requests.map { request ->
            async { userService.createUser(request) }
        }.awaitAll()

        assertEquals(10, users.size)
        assertEquals(10, userRepository.findAll().size)

        // Verify all users are unique
        val emails = users.map { it.email }.toSet()
        assertEquals(10, emails.size)
    }
}
```

## Best Practices

### Schema Design
```kotlin
// Хорошо структурированные таблицы
object UsersTable : IntIdTable("users") {
    val email = varchar("email", 255).uniqueIndex("idx_users_email")
    val name = varchar("name", 100).index("idx_users_name")
    val age = integer("age").nullable()
    val status = enumerationByName("status", 20, UserStatus::class)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime())
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime())

    // Constraints
    init {
        check { age greaterEq 0 }
    }
}

enum class UserStatus {
    ACTIVE, INACTIVE, SUSPENDED
}

// Связи между таблицами
object PostsTable : IntIdTable("posts") {
    val userId = reference("user_id", UsersTable, onDelete = ReferenceOption.CASCADE)
    val title = varchar("title", 200)
    val content = text("content")
    val published = bool("published").default(false)

    // Composite index
    init {
        index("idx_posts_user_published", userId, published)
    }
}
```

### Query Optimization
```kotlin
object QueryOptimization {

    // Использование индексов
    fun findActiveUsers(): List<UserEntity> {
        return UserEntity.find {
            UsersTable.status eq UserStatus.ACTIVE
        }.toList()
    }

    // Пагинация
    fun findUsersPaginated(page: Int, size: Int): List<UserEntity> {
        return UserEntity.find { UsersTable.status eq UserStatus.ACTIVE }
            .limit(size, offset = (page * size).toLong())
            .toList()
    }

    // Eager loading для отношений
    fun findUserWithPosts(userId: Int): UserWithPosts? {
        return UserEntity.findById(userId)?.let { user ->
            UserWithPosts(
                user = user,
                posts = PostEntity.find { PostsTable.userId eq user.id }
                    .limit(10)
                    .toList()
            )
        }
    }

    // Batch operations
    fun updateUserStatuses(userIds: List<Int>, newStatus: UserStatus) {
        transaction {
            UsersTable.update({ UsersTable.id inList userIds }) {
                it[UsersTable.status] = newStatus
            }
        }
    }
}
```

### Error Handling
```kotlin
sealed class DatabaseError : Exception() {
    data class NotFound(val entity: String, val id: Any) : DatabaseError()
    data class DuplicateKey(val field: String, val value: Any) : DatabaseError()
    data class ValidationError(val field: String, val message: String) : DatabaseError()
    data class ConnectionError(val cause: Throwable) : DatabaseError()
}

suspend fun <T> safeTransaction(block: Transaction.() -> T): Result<T> {
    return try {
        val result = newSuspendedTransaction { block() }
        Result.success(result)
    } catch (e: ExposedSQLException) {
        when (e.sqlState) {
            "23505" -> Result.failure(DatabaseError.DuplicateKey("email", "unknown"))
            "23503" -> Result.failure(DatabaseError.NotFound("entity", "unknown"))
            else -> Result.failure(DatabaseError.ConnectionError(e))
        }
    } catch (e: Exception) {
        Result.failure(DatabaseError.ConnectionError(e))
    }
}

// Usage
suspend fun createUser(request: CreateUserRequest): Result<User> {
    return safeTransaction {
        // Database operations
        val user = UserEntity.new {
            name = request.name
            email = request.email
        }
        user.toUser()
    }
}
```

## Migration и Schema Evolution

### Schema Migrations
```kotlin
object DatabaseMigrations {

    fun migrateFromV1ToV2() {
        transaction {
            // Add new columns
            if (!UsersTable.age.exists()) {
                exec("ALTER TABLE users ADD COLUMN age INT")
            }

            if (!UsersTable.status.exists()) {
                exec("ALTER TABLE users ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE'")
            }

            // Create new tables
            SchemaUtils.createMissingTablesAndColumns(PostsTable)
        }
    }

    fun migrateFromV2ToV3() {
        transaction {
            // Add indexes for performance
            exec("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)")
            exec("CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts(user_id)")

            // Update data
            exec("UPDATE users SET status = 'ACTIVE' WHERE status IS NULL")
        }
    }
}

// Flyway integration
@Configuration
class FlywayConfig {

    @Bean
    fun flyway(dataSource: DataSource): Flyway {
        return Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
    }

    @Bean
    @DependsOn("flyway")
    fun database(flyway: Flyway, dataSource: DataSource): Database {
        flyway.migrate() // Run migrations first
        return Database.connect(dataSource)
    }
}
```

## Performance Considerations

### Connection Pooling
```kotlin
@Configuration
class HikariConfig {

    @Bean
    fun dataSource(): HikariDataSource {
        return HikariDataSource().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/mydb"
            username = "user"
            password = "password"

            // Connection pool settings
            maximumPoolSize = 20
            minimumIdle = 5
            idleTimeout = 300000
            maxLifetime = 600000
            connectionTimeout = 30000

            // Prepared statement caching
            dataSourceProperties.apply {
                setProperty("cachePrepStmts", "true")
                setProperty("prepStmtCacheSize", "250")
                setProperty("prepStmtCacheSqlLimit", "2048")
            }
        }
    }
}
```

### Query Optimization
```kotlin
object QueryOptimization {

    // Использование prepared statements (автоматически в Exposed)
    fun findUsersByIds(ids: List<Int>): List<UserEntity> {
        return UserEntity.find { UsersTable.id inList ids }
            .toList()
    }

    // Batch operations
    fun insertUsers(users: List<User>): List<UserEntity> {
        return transaction {
            UserEntity.batchInsert(users) { user ->
                this[UsersTable.name] = user.name
                this[UsersTable.email] = user.email
                this[UsersTable.age] = user.age
            }.map { UserEntity.wrapRow(it) }
        }
    }

    // Lazy loading
    fun getUsersWithLazyPosts(): List<UserWithLazyPosts> {
        return UserEntity.all().map { user ->
            UserWithLazyPosts(
                user = user,
                postsLoader = { user.posts.toList() }
            )
        }
    }

    // Streaming для больших результатов
    fun streamUsers(): Sequence<UserEntity> {
        return UserEntity.find { UsersTable.status eq UserStatus.ACTIVE }
            .asSequence()
    }
}
```

## Troubleshooting

### Common Issues
```kotlin
object ExposedTroubleshooting {

    // Проблема: Connection leaks
    suspend fun withTransaction(block: Transaction.() -> Unit) {
        newSuspendedTransaction {
            try {
                block()
                commit() // Явный commit
            } catch (e: Exception) {
                rollback() // Явный rollback
                throw e
            }
        }
    }

    // Проблема: N+1 queries
    fun getUsersWithPosts(): List<UserWithPosts> {
        return UserEntity.all().map { user ->
            // Плохо: отдельный запрос для каждого пользователя
            UserWithPosts(user, user.posts.toList())
        }
    }

    fun getUsersWithPostsOptimized(): List<UserWithPosts> {
        // Хорошо: один запрос с join
        val userPosts = (UsersTable leftJoin PostsTable)
            .selectAll()
            .groupBy { it[UsersTable.id] }
            .mapValues { (_, rows) ->
                val user = rows.first().let {
                    UserEntity.wrapRow(it)
                }
                val posts = rows.mapNotNull { row ->
                    if (row.getOrNull(PostsTable.id) != null) {
                        PostEntity.wrapRow(row)
                    } else null
                }
                UserWithPosts(user, posts)
            }

        return userPosts.values.toList()
    }

    // Проблема: Case sensitivity в PostgreSQL
    fun caseInsensitiveSearch(name: String): List<UserEntity> {
        return UserEntity.find {
            LowerCase(UsersTable.name) like "%${name.lowercase()}%"
        }
    }

    // Проблема: Date/Time handling
    fun findUsersBornAfter(date: LocalDate): List<UserEntity> {
        return UserEntity.find {
            UsersTable.birthDate greaterEq date
        }
    }
}
```

### Debugging Queries
```kotlin
object QueryDebugging {

    // Логирование SQL запросов
    fun enableQueryLogging() {
        org.jetbrains.exposed.sql.addLogger(StdOutSqlLogger)
    }

    // Кастомный логгер
    object CustomLogger : SqlLogger {
        override fun log(context: SqlLoggerContext, transaction: Transaction) {
            println("SQL: ${context.statement}")
            println("Parameters: ${context.parameters}")
            println("Duration: ${context.duration}ms")
        }
    }

    fun enableCustomLogging() {
        addLogger(CustomLogger)
    }

    // Анализ производительности запросов
    suspend fun profileQuery(block: suspend () -> Unit) {
        val startTime = System.nanoTime()
        block()
        val duration = (System.nanoTime() - startTime) / 1_000_000
        println("Query took: ${duration}ms")
    }

    // Использование
    suspend fun profiledOperation() = profileQuery {
        transaction {
            UserEntity.all().count()
        }
    }
}
```

## Migration Guide

### From JDBC to Exposed
```kotlin
// JDBC approach
fun findUserById(id: Int): User? {
    val sql = "SELECT * FROM users WHERE id = ?"
    return jdbcTemplate.query(sql, { rs, _ ->
        User(rs.getInt("id"), rs.getString("name"), rs.getString("email"))
    }, id).firstOrNull()
}

// Exposed approach
fun findUserById(id: Int): User? {
    return transaction {
        Users.select { Users.id eq id }
            .singleOrNull()
            ?.let {
                User(it[Users.id], it[Users.name], it[Users.email])
            }
    }
}
```

### From Hibernate to Exposed
```kotlin
// Hibernate entity
@Entity
data class User(
    @Id @GeneratedValue
    val id: Long = 0,
    val name: String,
    val email: String
)

// Hibernate repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
}

// Exposed approach
object UsersTable : IntIdTable("users") {
    val name = varchar("name", 100)
    val email = varchar("email", 100).uniqueIndex()
}

class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(UsersTable)
    var name by UsersTable.name
    var email by UsersTable.email
}

object UserRepository {
    fun findByEmail(email: String): UserEntity? {
        return UserEntity.find { UsersTable.email eq email }.singleOrNull()
    }
}
```

## Experimental Features

### Exposed 0.40+ Features (Future)
```kotlin
// Предполагаемые возможности Exposed 0.40+

// Улучшенная поддержка JSON columns
object JsonExampleTable : IntIdTable() {
    val metadata = json<Map<String, Any>>("metadata")
}

// Использование
val entity = JsonExampleTable.insertAndGetId {
    it[metadata] = mapOf("version" to "1.0", "features" to listOf("auth", "logging"))
}

// Type-safe JSON queries (предполагаемо)
val entities = JsonExampleTable.select {
    metadata.jsonExtract<String>("version") eq "1.0"
}

// Улучшенная поддержка enums
enum class Priority { LOW, MEDIUM, HIGH }

object TasksTable : IntIdTable() {
    val priority = enumeration<Priority>("priority")
}

// Улучшенная поддержка массивов
object ArrayExampleTable : IntIdTable() {
    val tags = array<String>("tags")
}

// Использование
ArrayExampleTable.insert {
    it[tags] = arrayOf("kotlin", "exposed", "database")
}

val kotlinEntities = ArrayExampleTable.select {
    tags contains "kotlin"
}
```

## Дата последнего обновления
22 января 2026 г.

## Полезные ссылки
- [Официальная документация Exposed](https://github.com/JetBrains/Exposed)
- [Exposed Wiki](https://github.com/JetBrains/Exposed/wiki)
- [Getting Started Guide](https://github.com/JetBrains/Exposed#quick-start)
- [DSL Reference](https://jetbrains.github.io/Exposed/)

## См. также
- [Kotlin Basics](kotlin-basics.md) - Основы Kotlin
- [HikariCP](java-hikaricp.md) - Connection pooling
- [PostgreSQL](databases/postgres/postgres-basics.md) - Работа с PostgreSQL
