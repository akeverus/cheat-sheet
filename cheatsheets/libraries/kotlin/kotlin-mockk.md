---
title: "MockK"
description: "MockK - это мощная mocking библиотека для Kotlin, предоставляющая продвинутые возможности для создания mock объектов в тестах. Поддерживает Kotlin-специфичные фичи, такие как inline функции, операторы и null safety."
tags: ["libraries", "kotlin", "kotlin-mockk"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# MockK

**MockK** - это мощная **mocking** библиотека для **Kotlin**, предоставляющая продвинутые возможности для создания **mock** объектов в тестах. Поддерживает **Kotlin**-специфичные фичи, такие как **inline** функции, операторы и **null safety**.

**Дата последнего обновления:** 2026-02-06

## Полезные ссылки

### Официальная документация
- [MockK](https://mockk.io/) — официальный сайт
- [MockK GitHub](https://github.com/mockk/mockk) — репозиторий проекта
- [MockK Documentation](https://mockk.io/doc/) — документация

### См. также
- [Mockito](../testing-libraries/java-mockito.md) — **Mockito** для **Java**
- [Unit-тестирование с MockK](../../testing/unit-testing/README.md) — **Unit** тестирование с **MockK**

## Содержание

- [Основные возможности](#основные-возможности)
  - [Создание Mock объектов](#создание-mock-объектов)
  - [Stubbing методов](#stubbing-методов)
  - [Verification (**проверка вызовов**)](#verification-проверка-вызовов)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Argument Matching](#argument-matching)
  - [Mocking Kotlin-специфичных конструкций](#mocking-kotlin-специфичных-конструкций)
  - [Coroutines support](#coroutines-support)
  - [DSL для сложных сценариев](#dsl-для-сложных-сценариев)
- [Testing Patterns](#testing-patterns)
  - [Unit Testing с MockK](#unit-testing-с-mockk)
  - [Integration Testing](#integration-testing)
  - [Data Class Testing](#data-class-testing)
- [Advanced Features](#advanced-features)
  - [Dynamic Mocking](#dynamic-mocking)
  - [Hierarchical Mocking](#hierarchical-mocking)
  - [Mocking Static Methods](#mocking-static-methods)
  - [Constructor Mocking](#constructor-mocking)
  - [Object Mocking](#object-mocking)
- [Spring Boot Integration](#spring-boot-integration)
  - [Testing Service Layer](#testing-service-layer)
  - [Testing Controller Layer](#testing-controller-layer)
  - [Testing Repository Layer](#testing-repository-layer)
- [Testing Best Practices](#testing-best-practices)
  - [Test Structure и Naming](#test-structure-и-naming)
  - [Custom Matchers и Assertions](#custom-matchers-и-assertions)
  - [Test Data Builders](#test-data-builders)
- [Performance Testing](#performance-testing)
  - [Benchmarking Mocks](#benchmarking-mocks)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Debugging MockK Tests](#debugging-mockk-tests)
- [Migration Guide](#migration-guide)
  - [From Mockito to MockK](#from-mockito-to-mockk)
  - [From EasyMock to MockK](#from-easymock-to-mockk)
  - [From PowerMock to MockK](#from-powermock-to-mockk)
- [Best Practices](#best-practices)
  - [When to use MockK](#when-to-use-mockk)
  - [Anti-patterns to avoid](#anti-patterns-to-avoid)
- [Experimental Features](#experimental-features)
  - [MockK 2.0+ **Features** (**Future**)](#mockk-20-features-future)

## Основные возможности

### Создание **Mock** объектов

Пример создания **mock**-объектов в **MockK** (**Kotlin**).

```kotlin
import io.mockk.*

/
 * Создание mock объектов в MockK
 * MockK предоставляет различные способы создания mock объектов
 * в зависимости от требований теста
 */
// Создание mock объекта
// mockk<T>() - создает mock объект типа T
// Все методы по умолчанию требуют явной настройки (stubbing)
val mockService = mockk<Service>()
// Если вызвать метод без stubbing, будет выброшено исключение

// Создание spy объекта
// spyk<T>() - создает spy объект (обертка над реальным объектом)
// Вызывает реальные методы, но позволяет их перехватывать и верифицировать
val spyService = spyk<Service>()
// Можно переопределить некоторые методы через every {}, остальные работают как обычно

// Создание relaxed mock (не требует предварительной настройки)
// relaxed = true - все методы возвращают значения по умолчанию без явного stubbing
val relaxedMock = mockk<Service>(relaxed = true)
// Удобно когда нужно быстро создать mock без настройки всех методов
// Возвращает: null для nullable типов, пустые коллекции, 0 для чисел, false для Boolean

// Создание mock с именем для отладки
// name = "userService" - имя mock объекта для отображения в логах и ошибках
val namedMock = mockk<Service>(name = "userService")
// Помогает идентифицировать mock в сложных тестах с множеством mock объектов
```

### **Stubbing** методов
```kotlin
/
 * Stubbing методов в MockK
 * Stubbing определяет поведение mock объекта при вызове методов
 */
val mockRepository = mockk<UserRepository>()

// Простое stubbing - возврат фиксированного значения
// every { } - блок для определения поведения метода
// returns - возвращает указанное значение
every { mockRepository.findById(1) } returns User(1, "John")
// При вызове findById(1) вернется User(1, "John")

// any() - матчер для любого значения аргумента
every { mockRepository.save(any()) } returns User(2, "Jane")
// При вызове save() с любым аргументом вернется User(2, "Jane")

// Stubbing с условиями - разные ответы для разных аргументов
every { mockRepository.findByEmail("john@example.com") } returns User(1, "John")
// or() - логическое ИЛИ для матчеров
// Возвращает null для email содержащих "admin@" или "test@"
every { mockRepository.findByEmail(or("admin@", "test@")) } returns null

// Stubbing для коллекций - возврат списка значений
every { mockRepository.findAll() } returns listOf(User(1, "John"), User(2, "Jane"))
// При каждом вызове findAll() вернется один и тот же список

// Stubbing с последовательными вызовами
// returnsMany - возвращает значения из списка последовательно при каждом вызове
every { mockRepository.getUserCount() } returnsMany listOf(0, 1, 2, 5)
// Первый вызов вернет 0, второй - 1, третий - 2, четвертый - 5
// Последующие вызовы вернут последнее значение (5)

// Stubbing с throws - выбрасывание исключения
every { mockRepository.deleteById(-1) } throws IllegalArgumentException("Invalid ID")
// При вызове deleteById(-1) будет выброшено исключение

// Stubbing с ответами через функцию
// answers { } - позволяет вычислить ответ динамически на основе аргументов
every { mockRepository.findByName(any()) } answers {
    // firstArg<T>() - получение первого аргумента вызова с приведением типа
    val name = firstArg<String>()
    // Возвращаем User с именем в верхнем регистре
    User(0, name.toUpperCase())
}
// При вызове findByName("john") вернется User(0, "JOHN")
```

### **Verification** (**проверка вызовов**)
```kotlin
val mockService = mockk<EmailService>()

// Проверка, что метод был вызван
verify { mockService.sendEmail("user@example.com", "Welcome!") }

// Проверка количества вызовов
verify(exactly = 2) { mockService.sendEmail(any(), any()) }
verify(atLeast = 1) { mockService.sendEmail(any(), any()) }
verify(atMost = 3) { mockService.sendEmail(any(), any()) }

// Проверка порядка вызовов
verifyOrder {
    mockService.sendWelcomeEmail(any())
    mockService.sendVerificationEmail(any())
}

// Проверка, что метод НЕ был вызван
verify(inverse = true) { mockService.sendSpam(any()) }

// Проверка таймингов
verify(timeout = 1000) { mockService.sendEmail(any(), any()) }
```

## Продвинутые возможности

### **Argument Matching**
```kotlin
val mockRepository = mockk<UserRepository>()

// Основные матчеры
every { mockRepository.findById(any()) } returns User(1, "Mock")
every { mockRepository.findById(isNull()) } returns null
every { mockRepository.findById(neq(0)) } returns User(1, "Valid")

// Строковые матчеры
every { mockRepository.findByEmail(matches(".*@example\\.com$")) } returns User(1, "Valid")
every { mockRepository.findByEmail(startsWith("admin")) } returns User(2, "Admin")

// Коллекционные матчеры
every { mockRepository.findByIds(any<List<Int>>()) } returns emptyList()
every { mockRepository.findByIds(hasSize(2)) } returns listOf(User(1, "A"), User(2, "B"))

// Кастомные матчеры
fun hasValidId() = match<User> { it.id > 0 }
every { mockRepository.save(hasValidId()) } returns User(1, "Saved")

// Capturing arguments
val capturedUsers = mutableListOf<User>()
every { mockRepository.save(capture(capturedUsers)) } returns User(1, "Saved")

// Slot capturing
val slot = slot<String>()
every { mockRepository.findByEmail(capture(slot)) } returns User(1, slot.captured)
```

### **Mocking Kotlin**-специфичных конструкций
```kotlin
// Mocking inline функций
inline fun <T> measureTime(block: () -> T): Pair<T, Long> {
    val start = System.nanoTime()
    val result = block()
    val time = System.nanoTime() - start
    return result to time
}

// Тестирование
@Test
fun `should measure execution time`() {
    val mockBlock = mockk<() -> String>()
    every { mockBlock() } returns "result"

    val (result, time) = measureTime(mockBlock)

    assertEquals("result", result)
    assertTrue(time >= 0)
    verify { mockBlock() }
}

// Mocking extension функций
fun String.isValidEmail(): Boolean = contains("@") && contains(".")

@Test
fun `should validate email using extension function`() {
    // Mocking extension function
    every { "test@example.com".isValidEmail() } returns true
    every { "invalid".isValidEmail() } returns false

    assertTrue("test@example.com".isValidEmail())
    assertFalse("invalid".isValidEmail())
}

// Mocking операторов
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
}

@Test
fun `should mock operator functions`() {
    val point1 = mockk<Point>()
    val point2 = Point(1, 2)

    every { point1 + point2 } returns Point(3, 4)

    val result = point1 + point2
    assertEquals(Point(3, 4), result)
}
```

### **Coroutines support**
```kotlin
import io.mockk.*
import kotlinx.coroutines.*

// Mocking suspend функций
interface ApiService {
    suspend fun getUser(id: Int): User
    suspend fun saveUser(user: User): User
}

@Test
fun `should handle suspend functions`() = runTest {
    val mockApi = mockk<ApiService>()

    coEvery { mockApi.getUser(1) } returns User(1, "John")
    coEvery { mockApi.saveUser(any()) } coAnswers {
        val user = firstArg<User>()
        user.copy(id = 2)
    }

    val user = mockApi.getUser(1)
    assertEquals(User(1, "John"), user)

    val saved = mockApi.saveUser(User(0, "Jane"))
    assertEquals(User(2, "Jane"), saved)

    coVerify { mockApi.getUser(1) }
    coVerify { mockApi.saveUser(any()) }
}

// Mocking Flow
interface DataRepository {
    fun getUsers(): Flow<List<User>>
}

@Test
fun `should handle Flow`() = runTest {
    val mockRepo = mockk<DataRepository>()

    every { mockRepo.getUsers() } returns flowOf(
        listOf(User(1, "John")),
        listOf(User(1, "John"), User(2, "Jane"))
    )

    val users = mockRepo.getUsers().toList()
    assertEquals(2, users.size)

    verify { mockRepo.getUsers() }
}
```

### **DSL** для сложных сценариев
```kotlin
@Test
fun `should handle complex interaction scenario`() {
    val mockService = mockk<UserService>()

    // DSL для описания поведения
    every {
        mockService.processUser(any(), any())
    } answers {
        val user = firstArg<User>()
        val action = secondArg<String>()

        when (action) {
            "validate" -> user.takeIf { it.name.isNotBlank() }
            "save" -> user.copy(id = 123)
            else -> throw IllegalArgumentException("Unknown action: $action")
        }
    }

    // Использование
    val user = User(0, "John")
    val validated = mockService.processUser(user, "validate")
    val saved = mockService.processUser(user, "save")

    assertNotNull(validated)
    assertEquals(123, saved?.id)
}
```

## **Testing Patterns**

### **Unit Testing** с **MockK**
```kotlin
class UserServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val emailService = mockk<EmailService>()
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository, emailService)
    }

    @Test
    fun `should create user successfully`() {
        // Given
        val user = User(0, "John", "john@example.com")
        val savedUser = user.copy(id = 1)

        every { userRepository.save(any()) } returns savedUser
        every { emailService.sendWelcomeEmail(any()) } returns Unit

        // When
        val result = userService.createUser(user)

        // Then
        assertEquals(savedUser, result)
        verifyOrder {
            userRepository.save(user)
            emailService.sendWelcomeEmail("john@example.com")
        }
    }

    @Test
    fun `should throw exception for invalid email`() {
        // Given
        val user = User(0, "John", "invalid-email")

        every { userRepository.save(any()) } throws IllegalArgumentException("Invalid email")

        // When & Then
        assertThrows<IllegalArgumentException> {
            userService.createUser(user)
        }

        verify { userRepository.save(user) }
        verify(exactly = 0) { emailService.sendWelcomeEmail(any()) }
    }

    @Test
    fun `should handle repository exceptions gracefully`() {
        // Given
        val user = User(0, "John", "john@example.com")

        every { userRepository.save(any()) } throws RuntimeException("Database error")

        // When & Then
        assertThrows<RuntimeException> {
            userService.createUser(user)
        }.also { exception ->
            assertEquals("Database error", exception.message)
        }
    }
}
```

### **Integration Testing**
```kotlin
@SpringBootTest
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    @Test
    fun `should create user via REST API`() {
        // Given
        val user = User(0, "John", "john@example.com")
        val createdUser = user.copy(id = 1)

        every { userService.createUser(any()) } returns createdUser

        // When & Then
        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "John",
                        "email": "john@example.com"
                    }
                """)
        )
        .andExpect(status().isCreated)
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John"))

        verify { userService.createUser(any()) }
    }
}
```

### **Data Class Testing**
```kotlin
@Test
fun `should handle data class operations`() {
    val mockList = mockk<MutableList<User>>(relaxed = true)

    // Тестирование copy
    val user = User(1, "John", "john@example.com")
    val updatedUser = user.copy(email = "john.doe@example.com")

    mockList.add(user)
    mockList.add(updatedUser)

    verify {
        mockList.add(user)
        mockList.add(updatedUser)
    }

    // Тестирование componentN
    val (id, name, email) = updatedUser
    assertEquals(1, id)
    assertEquals("John", name)
    assertEquals("john.doe@example.com", email)
}
```

## **Advanced Features**

### **Dynamic Mocking**
```kotlin
@Test
fun `should handle dynamic mocking scenarios`() {
    val mockService = mockk<UserService>(relaxed = true)

    // Dynamic call recording
    every { mockService.getUser(any()) } answers {
        val id = firstArg<Int>()
        User(id, "User$id", "user$id@example.com")
    }

    // Test multiple calls
    val user1 = mockService.getUser(1)
    val user2 = mockService.getUser(2)

    assertEquals("User1", user1.name)
    assertEquals("User2", user2.name)

    // Verify calls
    verify(exactly = 2) { mockService.getUser(any()) }
}
```

### **Hierarchical Mocking**
```kotlin
data class Company(val name: String, val departments: List<Department>)
data class Department(val name: String, val employees: List<Employee>)
data class Employee(val name: String, val salary: Double)

@Test
fun `should handle hierarchical data structures`() {
    val mockRepository = mockk<CompanyRepository>(relaxed = true)

    val employee = Employee("John", 50000.0)
    val department = Department("IT", listOf(employee))
    val company = Company("TechCorp", listOf(department))

    every { mockRepository.findByName("TechCorp") } returns company

    val found = mockRepository.findByName("TechCorp")

    assertEquals("TechCorp", found?.name)
    assertEquals(1, found?.departments?.size)
    assertEquals("John", found?.departments?.first()?.employees?.first()?.name)
}
```

### **Mocking Static Methods**
```kotlin
object Utils {
    fun formatDate(date: LocalDate): String = date.toString()
    @JvmStatic
    fun isValidEmail(email: String): Boolean = email.contains("@")
}

// В build.gradle.kts
dependencies {
    testImplementation("io.mockk:mockk:1.13.5")
}

// В тесте
@Test
fun `should mock static methods`() {
    mockkStatic("com.example.UtilsKt") // или Utils::class

    every { Utils.formatDate(any()) } returns "2023-01-01"
    every { Utils.isValidEmail("test@example.com") } returns true
    every { Utils.isValidEmail("invalid") } returns false

    assertEquals("2023-01-01", Utils.formatDate(LocalDate.now()))
    assertTrue(Utils.isValidEmail("test@example.com"))
    assertFalse(Utils.isValidEmail("invalid"))

    unmockkStatic("com.example.UtilsKt")
}
```

### **Constructor Mocking**
```kotlin
class DatabaseConnection(url: String) {
    fun connect(): Boolean = true
    fun disconnect() {}
}

@Test
fun `should mock constructor calls`() {
    mockkConstructor(DatabaseConnection::class)

    every { anyConstructed<DatabaseConnection>().connect() } returns true
    every { anyConstructed<DatabaseConnection>().disconnect() } just Runs

    // Использование
    val connection = DatabaseConnection("jdbc:test")
    assertTrue(connection.connect())
    connection.disconnect()

    verify {
        anyConstructed<DatabaseConnection>().connect()
        anyConstructed<DatabaseConnection>().disconnect()
    }

    unmockkConstructor(DatabaseConnection::class)
}
```

### **Object Mocking**
```kotlin
object Configuration {
    val databaseUrl: String = "jdbc:h2:mem:test"
    fun getTimeout(): Long = 5000L
}

@Test
fun `should mock object methods and properties`() {
    mockkObject(Configuration)

    every { Configuration.databaseUrl } returns "jdbc:postgresql://test"
    every { Configuration.getTimeout() } returns 10000L

    assertEquals("jdbc:postgresql://test", Configuration.databaseUrl)
    assertEquals(10000L, Configuration.getTimeout())

    unmockkObject(Configuration)
}
```

## **Spring Boot Integration**

### **Testing Service Layer**
```kotlin
@Service
class UserService(
    private val userRepository: UserRepository,
    private val emailService: EmailService
) {
    fun createUser(user: User): User {
        val savedUser = userRepository.save(user)
        emailService.sendWelcomeEmail(savedUser.email)
        return savedUser
    }

    fun getUser(id: Long): User? = userRepository.findById(id)

    fun updateUser(id: Long, updates: Map<String, Any>): User? {
        val user = userRepository.findById(id) ?: return null

        val updatedUser = user.copy(
            name = updates["name"] as? String ?: user.name,
            email = updates["email"] as? String ?: user.email
        )

        return userRepository.save(updatedUser)
    }
}

class UserServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val emailService = mockk<EmailService>()
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository, emailService)
    }

    @Test
    fun `should create user successfully`() {
        val user = User(0, "John", "john@example.com")
        val savedUser = user.copy(id = 1)

        every { userRepository.save(user) } returns savedUser
        every { emailService.sendWelcomeEmail("john@example.com") } just Runs

        val result = userService.createUser(user)

        assertEquals(savedUser, result)

        verifyOrder {
            userRepository.save(user)
            emailService.sendWelcomeEmail("john@example.com")
        }
    }

    @Test
    fun `should return null for non-existent user`() {
        every { userRepository.findById(999) } returns null

        val result = userService.getUser(999)

        assertNull(result)
        verify { userRepository.findById(999) }
    }

    @Test
    fun `should update user with partial data`() {
        val existingUser = User(1, "John", "john@example.com")
        val updates = mapOf("name" to "John Doe")

        every { userRepository.findById(1) } returns existingUser
        every { userRepository.save(any()) } answers { firstArg() }

        val result = userService.updateUser(1, updates)

        assertNotNull(result)
        assertEquals("John Doe", result?.name)
        assertEquals("john@example.com", result?.email)
    }
}
```

### **Testing Controller Layer**
```kotlin
@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService

    @Test
    fun `should create user via API`() {
        val user = User(0, "John", "john@example.com")
        val createdUser = user.copy(id = 1)

        every { userService.createUser(any()) } returns createdUser

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name":"John","email":"john@example.com"}""")
        )
        .andExpect(status().isCreated)
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("John"))

        verify { userService.createUser(any()) }
    }

    @Test
    fun `should return user by id`() {
        val user = User(1, "John", "john@example.com")

        every { userService.getUser(1) } returns user

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("John"))

        verify { userService.getUser(1) }
    }

    @Test
    fun `should return 404 for non-existent user`() {
        every { userService.getUser(999) } returns null

        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound)

        verify { userService.getUser(999) }
    }
}
```

### **Testing Repository Layer**
```kotlin
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @MockkBean
    private lateinit var auditorAware: AuditorAware<User>

    @BeforeEach
    fun setUp() {
        every { auditorAware.currentAuditor } returns Optional.of(User(0, "System", "system@example.com"))
    }

    @Test
    fun `should save and retrieve user`() {
        val user = User(0, "John", "john@example.com")

        val saved = userRepository.save(user)
        val retrieved = userRepository.findById(saved.id!!)

        assertNotNull(retrieved)
        assertEquals("John", retrieved.get().name)
        assertEquals("john@example.com", retrieved.get().email)
    }

    @Test
    fun `should find users by email domain`() {
        val users = listOf(
            User(0, "John", "john@example.com"),
            User(0, "Jane", "jane@test.com"),
            User(0, "Bob", "bob@example.com")
        )

        userRepository.saveAll(users)

        val exampleUsers = userRepository.findByEmailEndingWith("@example.com")

        assertEquals(2, exampleUsers.size)
        assertTrue(exampleUsers.all { it.email.endsWith("@example.com") })
    }
}
```

## **Testing Best Practices**

### **Test Structure** и **Naming**
```kotlin
class UserServiceTest : BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val emailService = mockk<EmailService>()
    lateinit var userService: UserService

    beforeEach {
        userService = UserService(userRepository, emailService)
    }

    given("a valid user") {
        val user = User(0, "John", "john@example.com")

        `when`("creating the user") {
            every { userRepository.save(any()) } returns user.copy(id = 1)
            every { emailService.sendWelcomeEmail(any()) } just Runs

            then("should save user and send email") {
                val result = userService.createUser(user)

                result.id shouldBe 1
                result.name shouldBe "John"

                verifyOrder {
                    userRepository.save(user)
                    emailService.sendWelcomeEmail("john@example.com")
                }
            }
        }

        `when`("user creation fails") {
            every { userRepository.save(any()) } throws RuntimeException("DB error")

            then("should not send email") {
                shouldThrow<RuntimeException> {
                    userService.createUser(user)
                }

                verify(exactly = 0) { emailService.sendWelcomeEmail(any()) }
            }
        }
    }
})
```

### **Custom Matchers** и **Assertions**
```kotlin
// Custom matchers
fun hasValidEmail() = match<User> { it.email.contains("@") }
fun hasPositiveId() = match<User> { it.id > 0 }

// Custom assertions
fun User.shouldBeValid() {
    this.id shouldBeGreaterThan 0
    this.name.shouldNotBeBlank()
    this.email.shouldContain("@")
}

// Использование
@Test
fun `should validate user creation`() {
    val user = User(0, "John", "john@example.com")

    every { userRepository.save(hasValidEmail()) } returns user.copy(id = 1)

    val result = userService.createUser(user)

    result.shouldBeValid()
    result.name shouldBe "John"
}
```

### **Test Data Builders**
```kotlin
class UserBuilder {
    private var id: Long = 0
    private var name: String = "Default Name"
    private var email: String = "default@example.com"

    fun id(id: Long) = apply { this.id = id }
    fun name(name: String) = apply { this.name = name }
    fun email(email: String) = apply { this.email = email }

    fun build() = User(id, name, email)

    companion object {
        fun validUser() = UserBuilder()
        fun userWithInvalidEmail() = UserBuilder().email("invalid")
        fun adminUser() = UserBuilder().name("Admin").email("admin@example.com")
    }
}

// Использование в тестах
@Test
fun `should create valid user`() {
    val user = UserBuilder.validUser()
        .name("John")
        .email("john@example.com")
        .build()

    every { userRepository.save(any()) } returns user.copy(id = 1)

    val result = userService.createUser(user)

    result.shouldBeValid()
}
```

## **Performance Testing**

### **Benchmarking Mocks**
```kotlin
@Test
fun `performance test with many mock calls`() {
    val mockService = mockk<UserService>(relaxed = true)

    val startTime = System.nanoTime()

    // Выполняем много вызовов
    repeat(10000) {
        mockService.getUser(it.toLong())
        mockService.createUser(User(0, "User$it", "user$it@example.com"))
    }

    val endTime = System.nanoTime()
    val durationMs = (endTime - startTime) / 1_000_000

    println("MockK performance test took: ${durationMs}ms")

    // Проверяем, что все вызовы были записаны
    verify(exactly = 10000) { mockService.getUser(any()) }
    verify(exactly = 10000) { mockService.createUser(any()) }
}
```

## Решение проблем

### **Common Issues**
```kotlin
class MockkTroubleshooting {

    @Test
    fun `fix missing stubbing issue`() {
        val mockService = mockk<UserService>()

        // Неправильно: вызов без предварительного stubbing
        // mockService.getUser(1) // Выбросит MissingMethodInvocationException

        // Правильно: сначала stubbing, потом вызов
        every { mockService.getUser(1) } returns User(1, "John")

        val user = mockService.getUser(1)
        assertEquals("John", user.name)
    }

    @Test
    fun `fix too many invocations issue`() {
        val mockService = mockk<UserService>()

        every { mockService.getUser(1) } returns User(1, "John")

        // Неправильно: ожидание 2 вызовов, но был только 1
        // verify(exactly = 2) { mockService.getUser(1) } // TooManyActualInvocations

        // Правильно: правильное количество вызовов
        mockService.getUser(1)
        mockService.getUser(1)

        verify(exactly = 2) { mockService.getUser(1) }
    }

    @Test
    fun `fix wrong argument matcher issue`() {
        val mockService = mockk<UserService>()

        // Неправильно: разные матчеры для разных вызовов
        // every { mockService.getUser(any()) } returns User(1, "John")
        // mockService.getUser(1)
        // verify { mockService.getUser(eq(2)) } // WrongArgumentMatcher

        // Правильно: соответствующие матчеры
        every { mockService.getUser(any()) } returns User(1, "John")

        mockService.getUser(1)

        verify { mockService.getUser(any()) }
        // или
        verify { mockService.getUser(1) }
    }

    @Test
    fun `fix relaxed mock issues`() {
        // Relaxed mock возвращает default значения
        val relaxedMock = mockk<UserService>(relaxed = true)

        // Эти вызовы не требуют предварительного stubbing
        val user = relaxedMock.getUser(1) // вернет null или default
        relaxedMock.createUser(User(0, "Test")) // ничего не делает

        // Но verification все равно работает
        verify { relaxedMock.getUser(1) }
        verify { relaxedMock.createUser(any()) }
    }
}
```

### **Debugging MockK Tests**
```kotlin
@Test
fun `debug mock interactions`() {
    val mockService = mockk<UserService>()

    // Включаем детальное логирование
    MockKSettings.useDebugLogging = true

    every { mockService.getUser(any()) } returns User(1, "John")
    every { mockService.createUser(any()) } returns User(2, "Jane")

    // Выполняем действия
    val user1 = mockService.getUser(1)
    val user2 = mockService.createUser(User(0, "Test"))

    // Проверяем все взаимодействия
    verifyAll {
        mockService.getUser(1)
        mockService.createUser(any())
    }

    // Получаем информацию о всех вызовах
    val allCalls = MockKGateway.implementation().recorder.callRecorder.calls
    println("Total calls: ${allCalls.size}")

    // Проверяем, что все stubbing был использован
    confirmVerified(mockService)
}
```

## **Migration Guide**

### **From Mockito** to **MockK**
```kotlin
// Mockito
`when`(mockService.getUser(1)).thenReturn(user)
verify(mockService).getUser(1)

// MockK
every { mockService.getUser(1) } returns user
verify { mockService.getUser(1) }
```

### **From EasyMock** to **MockK**
```kotlin
// EasyMock
EasyMock.expect(mockService.getUser(1)).andReturn(user)
EasyMock.replay(mockService)
EasyMock.verify(mockService)

// MockK
every { mockService.getUser(1) } returns user
verify { mockService.getUser(1) }
```

### **From PowerMock** to **MockK**
```kotlin
// PowerMock
@RunWith(PowerMockRunner.class)
@PrepareForTest(StaticClass.class)
public class Test {
    @Test
    public void testStatic() {
        PowerMockito.mockStatic(StaticClass.class);
        PowerMockito.when(StaticClass.staticMethod()).thenReturn("mocked");
    }
}

// MockK
@Test
fun testStatic() {
    mockkStatic("com.example.StaticClass")
    every { StaticClass.staticMethod() } returns "mocked"
}
```

## Лучшие практики

### **When** to **use MockK**
```kotlin
// Используйте MockK когда:

// 1. Тестируете Kotlin код
@Test
fun `kotlin specific features work great with MockK`() {
    val mockList = mockk<MutableList<String>>()

    every { mockList += "item" } just Runs
    every { mockList.contains(any()) } returns true

    mockList += "item"
    assertTrue(mockList.contains("test"))

    verify { mockList += "item" }
    verify { mockList.contains("test") }
}

// 2. Работаете с корутинами
@Test
fun `coroutines work seamlessly`() = runTest {
    val mockApi = mockk<ApiService>()

    coEvery { mockApi.fetchData() } returns "mocked data"

    val result = mockApi.fetchData()

    assertEquals("mocked data", result)
    coVerify { mockApi.fetchData() }
}

// 3. Нужны продвинутые матчеры
@Test
fun `advanced matching capabilities`() {
    val mockValidator = mockk<Validator>()

    every { mockValidator.validate(match { it.length > 5 }) } returns true
    every { mockValidator.validate(match { it.length <= 5 }) } returns false

    assertTrue(mockValidator.validate("long text"))
    assertFalse(mockValidator.validate("short"))
}

// 4. Тестируете DSL или операторы
@Test
fun `DSL and operators work perfectly`() {
    val mockBuilder = mockk<StringBuilder>()

    every { mockBuilder.append(any<String>()) } returns mockBuilder
    every { mockBuilder.toString() } returns "mocked result"

    val result = mockBuilder.append("test").toString()

    assertEquals("mocked result", result)
}
```

### **Anti-patterns** to **avoid**
```kotlin
@Test
fun `avoid over-mocking`() {
    // Плохо: слишком много mock объектов
    val mockRepo = mockk<UserRepository>()
    val mockEmail = mockk<EmailService>()
    val mockLogger = mockk<Logger>()
    val mockValidator = mockk<Validator>()

    // Лучше: используйте реальные объекты где возможно
    // или группируйте mocks
}

@Test
fun `avoid complex stubbing`() {
    val mockService = mockk<UserService>()

    // Плохо: слишком сложное stubbing
    every { mockService.processUser(any()) } answers {
        val user = firstArg<User>()
        when {
            user.name.isEmpty() -> throw IllegalArgumentException("Empty name")
            user.email.contains("@") -> user.copy(id = 1)
            else -> throw IllegalArgumentException("Invalid email")
        }
    }

    // Лучше: упростите логику или разделите на отдельные тесты
}

@Test
fun `prefer relaxed mocks for integration tests`() {
    // Хорошо для integration тестов
    val mockService = mockk<UserService>(relaxed = true)

    // Не требует предварительного stubbing всех методов
    mockService.getUser(1) // OK
    mockService.createUser(User(0, "Test")) // OK
}
```

## **Experimental Features**

### **MockK** 2.0+ **Features** (**Future**)
```kotlin
// Предполагаемые возможности MockK 2.0+

// Улучшенная поддержка inline классов
@JvmInline
value class Email(val value: String)

@Test
fun `should handle inline classes`() {
    val mockService = mockk<UserService>()

    every { mockService.sendEmail(Email("test@example.com")) } just Runs

    mockService.sendEmail(Email("test@example.com"))

    verify { mockService.sendEmail(Email("test@example.com")) }
}

// Context receivers support (Kotlin 1.6.20+)
// Улучшенная поддержка контекстных функций

// Native Kotlin/JS support
// Лучшая поддержка Kotlin Multiplatform

// Advanced coroutine testing features
@Test
fun `advanced coroutine testing`() = runTest {
    val mockFlowService = mockk<FlowService>()

    // Mocking Flow с custom timing
    coEvery { mockFlowService.getDataFlow() } returns flow {
        emit("first")
        delay(100)
        emit("second")
    }

    val results = mockFlowService.getDataFlow().toList()

    assertEquals(listOf("first", "second"), results)
}
```


## Полезные ссылки
- [Официальная документация `MockK`](https://mockk.io/)
- [MockK GitHub](https://github.com/mockk/mockk)
- [MockK Wiki](https://github.com/mockk/mockk/wiki)
- [Kotlin Testing](https://kotlinlang.org/docs/jvm-test-using-junit.html)

## См. также
- [JUnit 5](../testing-libraries/java-junit5.md) — Тестирование с **JUnit** 5
- [Kotlin Coroutines](kotlin-kotlinx-coroutines.md) — Асинхронное программирование
- [AssertJ](../../testing/unit-testing/junit/assertj.md) — **Assertions** для тестов

