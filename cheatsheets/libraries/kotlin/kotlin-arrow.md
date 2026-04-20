---
title: "Arrow"
description: "Arrow - это функциональная библиотека для Kotlin, предоставляющая типы для функционального программирования, такие как Either, Option, Try, IO и другие. Библиотека следует принципам функционального программирования и обеспечивает type safety."
tags:
  - libraries
  - kotlin
  - kotlin-arrow
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Arrow

**Arrow** - это функциональная библиотека для **Kotlin**, предоставляющая типы для функционального программирования, такие как **Either**, **Option**, **Try**, `IO` и другие. Библиотека следует принципам функционального программирования и обеспечивает **type safety**.

## Полезные ссылки

### Официальная документация
- [Arrow](https://arrow-kt.io/) — официальный сайт
- [Arrow GitHub](https://github.com/arrow-kt/arrow) — репозиторий проекта
- [Arrow Documentation](https://arrow-kt.io/docs/) — документация

### См. также
- [[java-vavr|Vavr]] — **Vavr** для функционального программирования в **Java**
- [[kotlin-kotlinx-coroutines|Kotlin Coroutines]] — **Kotlin Coroutines**

## Содержание

- [Основные возможности](#основные-возможности)
  - [Either (**для обработки ошибок**)](#either-для-обработки-ошибок)
  - [Option (**nullable safety**)](#option-nullable-safety)
  - [Try (**обработка исключений**)](#try-обработка-исключений)
  - [IO (управление side effects)](#io-управление-side-effects)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Validated (**накопление ошибок**)](#validated-накопление-ошибок)
  - [NonEmptyList (**непустые коллекции**)](#nonemptylist-непустые-коллекции)
  - [Eval (**ленивые вычисления**)](#eval-ленивые-вычисления)
  - [Continuation (**CPS**)](#continuation-cps)
- [Работа с коллекциями](#работа-с-коллекциями)
  - [Sequence comprehensions](#sequence-comprehensions)
  - [Extensions для стандартных коллекций](#extensions-для-стандартных-коллекций)
- [Optics (**линзы**)](#optics-линзы)
  - [Lens (**для immutable updates**)](#lens-для-immutable-updates)
  - [Prism (**для работы с sealed classes**)](#prism-для-работы-с-sealed-classes)
- [Integration с Kotlin](#integration-с-kotlin)
  - [KotlinX Coroutines integration](#kotlinx-coroutines-integration)
  - [Kotlin Serialization integration](#kotlin-serialization-integration)
- [Spring Boot Integration](#spring-boot-integration)
  - [Service Layer с Arrow](#service-layer-с-arrow)
  - [Controller с Arrow](#controller-с-arrow)
- [Testing с Arrow](#testing-с-arrow)
  - [Testing Either](#testing-either)
  - [Testing Validated](#testing-validated)
- [Best Practices](#best-practices)
  - [Error Handling Patterns](#error-handling-patterns)
  - [Type Safety Improvements](#type-safety-improvements)
  - [Performance Considerations](#performance-considerations)
- [Migration Guide](#migration-guide)
  - [From try-catch to Either](#from-try-catch-to-either)
  - [From null checks to Option](#from-null-checks-to-option)
  - [From CompletableFuture to IO](#from-completablefuture-to-io)
- [Troubleshooting](#troubleshooting)
  - [Common Issues](#common-issues)
  - [Debugging Arrow Code](#debugging-arrow-code)
- [Experimental Features](#experimental-features)
  - [Arrow Meta (**compile-time**)](#arrow-meta-compile-time)
  - [Arrow Fx Toolkit](#arrow-fx-toolkit)

## Основные возможности

### **Either** (**для обработки ошибок**)

**Either**: **Left** (**ошибка**) / **Right** (**успех**), **flatMap**, **getOrElse** и **pattern matching**.

```kotlin
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.getOrElse

/
 * Демонстрация работы с Either в Arrow
 * Either представляет значение которое может быть либо Left (ошибка), либо Right (успех)
 */
// Создание Either - Right для успешного значения, Left для ошибки
val success: Either<String, Int> = Either.Right(42)  // Успешное значение 42
val failure: Either<String, Int> = Either.Left("Error occurred")  // Ошибка со строковым сообщением

// Работа с Either - функция возвращает Either вместо бросания исключения
fun divide(a: Int, b: Int): Either<String, Int> =
    if (b == 0) Either.Left("Division by zero")  // Возвращаем Left при ошибке (деление на ноль)
    else Either.Right(a / b)  // Возвращаем Right с результатом при успехе

// Использование - цепочка операций с Either
val result = divide(10, 2)  // Right(5)
    .flatMap { divide(it, 2) }  // flatMap применяет функцию к Right значению, пропускает Left
    // Результат: Right(2) (10/2=5, затем 5/2=2)
    .getOrElse { 0 }  // getOrElse возвращает значение из Right или значение по умолчанию (0) если Left
// Если любая операция вернет Left, цепочка остановится и getOrElse вернет 0

// Pattern matching - проверка типа Either (Left или Right)
val message = when (result) {
    is Either.Left -> "Error: ${result.value}"  // Обработка ошибки - выводим сообщение об ошибке
    is Either.Right -> "Success: ${result.value}"  // Обработка успеха - выводим значение
}

// Map и fold - функциональные операции над Either
val doubled = result.map { it * 2 }  // map применяет функцию только к Right значению, Left остается без изменений
// Если result = Right(2), то doubled = Right(4)
// Если result = Left("error"), то doubled = Left("error")

val finalResult = result.fold(
    { error -> "Failed with: $error" },  // Функция для Left (обработка ошибки)
    { value -> "Success with: $value" }   // Функция для Right (обработка успеха)
)
// fold объединяет обе ветки Either в одно значение
```

### **Option** (**nullable safety**)
```kotlin
import arrow.core.Option
import arrow.core.none
import arrow.core.some

/
 * Демонстрация работы с Option в Arrow
 * Option представляет значение которое может быть либо Some (значение есть), либо None (значения нет)
 */
// Создание Option - some для значения, none для отсутствия значения
val someValue: Option<String> = some("Hello")  // Option с значением "Hello"
val noValue: Option<String> = none()  // Option без значения (аналог null)

// Безопасная работа с nullable значениями - преобразование nullable в Option
fun findUser(id: Int): Option<User> =
    users.find { it.id == id }?.let { some(it) } ?: none()
// find возвращает User? (nullable), преобразуем в Option через some/none
// Если пользователь найден - some(it), если нет - none()

// Использование - функциональные операции над Option
val user = findUser(123)  // Option<User> - может быть Some(User) или None

// map применяет функцию к значению внутри Some, None остается None
val name = user.map { it.name }.getOrElse { "Unknown" }
// Если user = Some(User(...)), то name = имя пользователя
// Если user = None, то name = "Unknown"

// flatMap для вложенных Option - извлекает Option из Option
val email = user.flatMap { it.email?.let { some(it) } ?: none() }
// Если user = Some(User) и user.email != null, то email = Some(email)
// Если user = None или user.email == null, то email = None

// Filter и exists - проверка условий
val hasValidEmail = user.exists { it.email?.isNotBlank() == true }
// exists проверяет условие для значения в Some, возвращает false для None
// Проверяем что email существует и не пустой

// Fold - объединение обеих веток Option в одно значение
val result = user.fold(
    { "User not found" },  // Функция для None (когда пользователь не найден)
    { "Found user: ${it.name}" }  // Функция для Some (когда пользователь найден)
)
// fold преобразует Option в строку: либо сообщение об ошибке, либо сообщение об успехе

// Pattern matching - проверка типа Option (Some или None)
val message = when (user) {
    is Some -> "User: ${user.value.name}"  // Обработка Some - выводим имя пользователя
    None -> "No user found"  // Обработка None - выводим сообщение об отсутствии
}
```

### **Try** (**обработка исключений**)
```kotlin
import arrow.core.Try
import arrow.core.Success
import arrow.core.Failure

// Создание Try из функции, которая может бросить исключение
fun riskyOperation(): Try<String> = Try {
    if (Random.nextBoolean()) {
        "Success"
    } else {
        throw RuntimeException("Something went wrong")
    }
}

// Использование
val result = riskyOperation()

// Pattern matching
val message = when (result) {
    is Success -> "Got: ${result.value}"
    is Failure -> "Error: ${result.exception.message}"
}

// Map и flatMap
val upperCase = result.map { it.toUpperCase() }
val chained = result.flatMap { value ->
    Try { value + " processed" }
}

// Recovery
val recovered = result.recover { ex ->
    "Recovered from: ${ex.message}"
}

val recoveredWith = result.recoverWith { ex ->
    Try { "Recovered: ${ex.message}" }
}

// Get or else
val safeResult = result.getOrElse { "Default value" }
val safeDefault = result.getOrDefault("Default value")
```

### `IO` (**управление side effects**)
```kotlin
import arrow.fx.IO
import arrow.fx.IO.Companion
import arrow.fx.extensions.fx

// Создание IO операций
val readFile: IO<String> = IO {
    File("data.txt").readText()
}

val writeFile: (String) -> IO<Unit> = { content ->
    IO { File("output.txt").writeText(content) }
}

val networkCall: IO<String> = IO {
    // Имитация сетевого вызова
    delay(1000)
    "Response from server"
}

// Композиция IO операций
val program: IO<String> = IO.fx {
    val data = !readFile
    val response = !networkCall
    val processed = "$data + $response"
    !writeFile(processed)
    processed
}

// Выполнение
program.attempt().unsafeRunSync().fold(
    { error -> println("Error: $error") },
    { result -> println("Success: $result") }
)

// Async IO
val asyncOperation: IO<String> = IO.async { callback ->
    thread {
        try {
            val result = expensiveOperation()
            callback(Right(result))
        } catch (e: Exception) {
            callback(Left(e))
        }
    }
}
```

## Продвинутые возможности

### **Validated** (**накопление ошибок**)
```kotlin
import arrow.core.Validated
import arrow.core.Valid
import arrow.core.Invalid
import arrow.core.valid
import arrow.core.invalid
import arrow.core.combine

// Валидация с накоплением ошибок
data class ValidationError(val field: String, val message: String)

fun validateName(name: String): Validated<ValidationError, String> =
    if (name.isNotBlank()) name.valid()
    else ValidationError("name", "Name cannot be blank").invalid()

fun validateAge(age: Int): Validated<ValidationError, Int> =
    if (age in 18..120) age.valid()
    else ValidationError("age", "Age must be between 18 and 120").invalid()

fun validateEmail(email: String): Validated<ValidationError, String> =
    if (email.contains("@")) email.valid()
    else ValidationError("email", "Invalid email format").invalid()

// Комбинирование валидаций
fun validateUser(name: String, age: Int, email: String):
        Validated<List<ValidationError>, User> {

    return Validated.combine(
        validateName(name),
        validateAge(age),
        validateEmail(email)
    ) { n, a, e -> User(n, a, e) }
}

// Использование
val validation = validateUser("John", 25, "john@example.com")

val result = when (validation) {
    is Valid -> "User created: ${validation.value}"
    is Invalid -> "Validation failed: ${validation.value.joinToString { it.message }}"
}
```

### **NonEmptyList** (**непустые коллекции**)
```kotlin
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf

// Создание NonEmptyList
val numbers = nonEmptyListOf(1, 2, 3, 4, 5)
val single = NonEmptyList.of(42)

// Операции
val head = numbers.head // Первый элемент
val tail = numbers.tail // Остальные элементы как List

// Безопасные операции
val max = numbers.maxOrNull()
val min = numbers.minOrNull()

// Преобразования
val doubled = numbers.map { it * 2 }
val evenNumbers = numbers.filter { it % 2 == 0 }

// Конвертация
val list = numbers.toList()
val array = numbers.toTypedArray()
```

### **Eval** (**ленивые вычисления**)
```kotlin
import arrow.core.Eval

// Создание Eval
val lazyValue: Eval<Int> = Eval.later {
    println("Computing...")
    expensiveComputation()
}

val memoizedValue: Eval<Int> = Eval.always {
    println("Computing...")
    expensiveComputation()
}

// Выполнение
val result1 = lazyValue.value() // Вычисляется каждый раз
val result2 = memoizedValue.value() // Вычисляется один раз и кешируется

// Композиция
val combined = lazyValue.map { it * 2 }.flatMap { value ->
    Eval.later { value + 10 }
}

// Stack-safe recursion
fun factorial(n: Int): Eval<Long> =
    if (n <= 1) Eval.now(1L)
    else Eval.defer { factorial(n - 1).map { it * n } }

val fact100 = factorial(100).value()
```

### **Continuation** (**CPS**)
```kotlin
import arrow.core.continuations.either
import arrow.core.continuations.option
import arrow.fx.coroutines.parZip

// Either continuation
suspend fun complexOperation(): Either<String, Int> = either {
    val a = riskyOp1().bind()
    val b = riskyOp2(a).bind()
    val c = riskyOp3(b).bind()
    c
}

// Option continuation
suspend fun safeOperation(): Option<Int> = option {
    val a = nullableOp1().bind()
    val b = nullableOp2(a).bind()
    b
}

// Parallel operations
suspend fun parallelOperations(): Either<String, Pair<String, String>> = either {
    parZip(
        { riskyStringOp1().bind() },
        { riskyStringOp2().bind() }
    ) { a, b -> a to b }
}
```

## Работа с коллекциями

### **Sequence comprehensions**
```kotlin
import arrow.core.sequence

// Создание последовательностей
val numbers = sequenceOf(1, 2, 3, 4, 5)

val evenSquares = numbers
    .filter { it % 2 == 0 }
    .map { it * it }

// Comprehension syntax (экспериментально)
val comprehension = sequence {
    val x = yield(1)
    val y = yield(2)
    val z = yield(3)
    x + y + z
}
```

### **Extensions** для стандартных коллекций
```kotlin
import arrow.core.filterOption
import arrow.core.flattenOption
import arrow.core.traverseOption

// Работа с Option в коллекциях
val listOfOptions = listOf(some(1), none(), some(3))
val flattened = listOfOptions.flattenOption() // [1, 3]

val numbers = listOf(1, 2, 3, 4, 5)
val evenOptions = numbers.map { if (it % 2 == 0) some(it) else none() }
val filtered = evenOptions.filterOption() // [2, 4]

// Traverse
val traversed = numbers.traverseOption { if (it > 0) some(it) else none() }
// Option<List<Int>> = Some([1, 2, 3, 4, 5])
```

## **Optics** (**линзы**)

### **Lens** (**для immutable updates**)
```kotlin
import arrow.optics.Lens
import arrow.optics.optics

@optics
data class Person(val name: String, val address: Address) {
    companion object
}

@optics
data class Address(val street: String, val city: String) {
    companion object
}

// Создание линз
val personNameLens = Person.name
val addressCityLens = Address.city
val personAddressLens = Person.address

// Композиция линз
val personCityLens = personAddressLens compose addressCityLens

// Использование
val person = Person("John", Address("Main St", "NYC"))

// Получение значения
val city = personCityLens.get(person) // "NYC"

// Обновление значения
val updatedPerson = personCityLens.set(person, "LA")
// Person(name=John, address=Address(street=Main St, city=LA))

// Модификация
val modifiedPerson = personCityLens.modify(person, String::toUpperCase)
// Person(name=John, address=Address(street=Main St, city=NYC))
```

### **Prism** (**для работы с sealed classes**)
```kotlin
import arrow.optics.Prism
import arrow.optics.optics

sealed class Shape {
    data class Circle(val radius: Double) : Shape()
    data class Rectangle(val width: Double, val height: Double) : Shape()
}

// Создание призм
val circlePrism = Prism(
    getOrModify = { shape ->
        when (shape) {
            is Shape.Circle -> Either.Right(shape.radius)
            else -> Either.Left(shape)
        }
    },
    reverseGet = { radius -> Shape.Circle(radius) }
)

// Использование
val circle = Shape.Circle(5.0)
val rectangle = Shape.Rectangle(10.0, 20.0)

// Получение значения
val circleRadius = circlePrism.getOrNull(circle) // 5.0
val rectRadius = circlePrism.getOrNull(rectangle) // null

// Reverse
val newCircle = circlePrism.reverseGet(10.0) // Circle(10.0)
```

## **Integration** с **Kotlin**

### **KotlinX Coroutines integration**
```kotlin
import arrow.fx.coroutines.*
import kotlinx.coroutines.*

// Arrow Fx с Coroutines
suspend fun arrowFxWithCoroutines(): String = effect {
    val a = effect { delay(100); "Hello" }.bind()
    val b = effect { delay(100); "World" }.bind()
    "$a $b"
}.fold(
    { error -> "Error: $error" },
    { result -> result }
)

// Resource management
val managedResource = resource {
    File("temp.txt").also { it.writeText("data") }
} release { file ->
    file.delete()
}

// Использование
val result = managedResource.use { file ->
    file.readText()
}
```

### **Kotlin Serialization integration**
```kotlin
import arrow.core.serialization.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

@Serializable
data class User(val name: String, val age: Int)

val user = User("John", 30)

// Either serialization
val eitherSerializer = EitherSerializer(String.serializer(), User.serializer())
val eitherJson = Json.encodeToString(eitherSerializer, Either.Right(user))

// Option serialization
val optionSerializer = OptionSerializer(User.serializer())
val optionJson = Json.encodeToString(optionSerializer, some(user))
```

## **Spring Boot Integration**

### **Service Layer** с **Arrow**
```kotlin
@Service
class UserService(
    private val userRepository: UserRepository,
    private val emailService: EmailService
) {

    suspend fun createUser(request: CreateUserRequest): Either<String, User> = either {
        val validatedUser = validateUser(request).bind()
        val savedUser = userRepository.save(validatedUser).toEither { "Save failed" }.bind()
        emailService.sendWelcomeEmail(savedUser.email).toEither { "Email failed" }.bind()
        savedUser
    }

    private fun validateUser(request: CreateUserRequest): Either<String, User> {
        return when {
            request.name.isBlank() -> Either.Left("Name is required")
            request.email.isBlank() -> Either.Left("Email is required")
            !request.email.contains("@") -> Either.Left("Invalid email format")
            else -> Either.Right(User(request.name, request.email))
        }
    }

    suspend fun getUser(id: Long): Option<User> =
        userRepository.findById(id)?.let { some(it) } ?: none()

    suspend fun updateUser(id: Long, request: UpdateUserRequest): Either<String, User> = either {
        val existingUser = userRepository.findById(id)?.let { some(it) } ?: none()
        val user = existingUser.toEither { "User not found" }.bind()

        val updatedUser = user.copy(
            name = request.name ?: user.name,
            email = request.email ?: user.email
        )

        userRepository.save(updatedUser).toEither { "Update failed" }.bind()
    }
}
```

### **Controller** с **Arrow**
```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    suspend fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<*> =
        userService.createUser(request).fold(
            { error -> ResponseEntity.badRequest().body(mapOf("error" to error)) },
            { user -> ResponseEntity.ok(user) }
        )

    @GetMapping("/{id}")
    suspend fun getUser(@PathVariable id: Long): ResponseEntity<*> =
        userService.getUser(id).fold(
            { ResponseEntity.notFound().build() },
            { user -> ResponseEntity.ok(user) }
        )

    @PutMapping("/{id}")
    suspend fun updateUser(
        @PathVariable id: Long,
        @RequestBody request: UpdateUserRequest
    ): ResponseEntity<*> =
        userService.updateUser(id, request).fold(
            { error -> ResponseEntity.badRequest().body(mapOf("error" to error)) },
            { user -> ResponseEntity.ok(user) }
        )
}
```

## **Testing** с **Arrow**

### **Testing Either**
```kotlin
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.assertions.arrow.core.shouldBeNone

class UserServiceTest {

    private val userRepository = mockk<UserRepository>()
    private val emailService = mockk<EmailService>()
    private val userService = UserService(userRepository, emailService)

    @Test
    fun `should create user successfully`() = runTest {
        // Given
        val request = CreateUserRequest("John", "john@example.com")
        val savedUser = User(1, "John", "john@example.com")

        coEvery { userRepository.save(any()) } returns savedUser
        coEvery { emailService.sendWelcomeEmail(any()) } returns Unit

        // When
        val result = userService.createUser(request)

        // Then
        result.shouldBeRight(savedUser)
        coVerify { userRepository.save(any()) }
        coVerify { emailService.sendWelcomeEmail("john@example.com") }
    }

    @Test
    fun `should return error for invalid email`() = runTest {
        // Given
        val request = CreateUserRequest("John", "invalid-email")

        // When
        val result = userService.createUser(request)

        // Then
        result.shouldBeLeft("Invalid email format")
    }

    @Test
    fun `should return none for non-existent user`() = runTest {
        // Given
        coEvery { userRepository.findById(999) } returns null

        // When
        val result = userService.getUser(999)

        // Then
        result.shouldBeNone()
    }
}
```

### **Testing Validated**
```kotlin
class ValidationTest {

    @Test
    fun `should validate user successfully`() {
        // Given
        val name = "John"
        val age = 25
        val email = "john@example.com"

        // When
        val result = validateUser(name, age, email)

        // Then
        result.shouldBeValid() // Extension from Kotest Arrow
        result.getOrNull()?.name shouldBe "John"
    }

    @Test
    fun `should accumulate validation errors`() {
        // Given
        val name = ""
        val age = 15
        val email = "invalid"

        // When
        val result = validateUser(name, age, email)

        // Then
        result.shouldBeInvalid()
        result.fold(
            { errors ->
                errors shouldHaveSize 3
                errors.map { it.field } shouldContainAll listOf("name", "age", "email")
            },
            { shouldNotReachHere() }
        )
    }
}
```

## Лучшие практики

### **Error Handling Patterns**
```kotlin
// Railway oriented programming
suspend fun processOrder(order: Order): Either<String, ProcessedOrder> = either {
    val validatedOrder = validateOrder(order).bind()
    val paymentResult = processPayment(validatedOrder).bind()
    val inventoryResult = reserveInventory(validatedOrder).bind()
    val shippingResult = arrangeShipping(validatedOrder).bind()

    ProcessedOrder(validatedOrder, paymentResult, inventoryResult, shippingResult)
}

// Resource management
val databaseTransaction = resource {
    transaction { /* start transaction */ }
} release { tx ->
    tx.rollback() // or commit
}

// Использование
databaseTransaction.use { tx ->
    // Работа с транзакцией
}

// Safe nullable operations
fun safeUserOperations(userId: String?): Option<UserDetails> = option {
    val id = userId.toLongOrNull().toOption().bind()
    val user = findUser(id).bind()
    val profile = loadUserProfile(user).bind()
    UserDetails(user, profile)
}
```

### **Type Safety Improvements**
```kotlin
// Использование Either вместо exceptions
data class HttpError(val code: Int, val message: String)

suspend fun apiCall(): Either<HttpError, ApiResponse> = either {
    val response = httpClient.get("/api/data").bind()
    val body = response.body<HttpError, ApiResponse>().bind()
    body
}

// Typed error handling
sealed class DomainError {
    data class ValidationError(val field: String, val message: String) : DomainError()
    data class NotFoundError(val resource: String) : DomainError()
    data class UnauthorizedError(val message: String) : DomainError()
}

suspend fun businessOperation(): Either<DomainError, Result> = either {
    val user = findUser(userId).toEither { DomainError.NotFoundError("User") }.bind()
    val validatedData = validateData(data).bind()
    val result = processData(validatedData).bind()
    result
}
```

### **Performance Considerations**
```kotlin
// Использование Eval для ленивых вычислений
val expensiveConfig: Eval<Config> = Eval.later {
    loadConfigurationFromFile()
}

// Кеширование результатов
val cachedResult: Eval<Result> = Eval.always {
    performExpensiveOperation()
}

// Stack-safe recursion
fun safeFactorial(n: Long): Eval<BigInteger> =
    if (n <= 1) Eval.now(BigInteger.ONE)
    else Eval.defer { safeFactorial(n - 1).map { it.multiply(BigInteger.valueOf(n)) } }
```

## **Migration Guide**

### **From try-catch** to **Either**
```kotlin
// Старый подход
fun divide(a: Int, b: Int): Int {
    if (b == 0) throw IllegalArgumentException("Division by zero")
    return a / b
}

try {
    val result = divide(10, 0)
    println("Result: $result")
} catch (e: IllegalArgumentException) {
    println("Error: ${e.message}")
}

// Новый подход с Arrow
fun divide(a: Int, b: Int): Either<String, Int> =
    if (b == 0) Either.Left("Division by zero")
    else Either.Right(a / b)

val result = divide(10, 0)
result.fold(
    { error -> println("Error: $error") },
    { value -> println("Result: $value") }
)
```

### **From null checks** to **Option**
```kotlin
// Старый подход
fun findUser(id: Int): User? = users.find { it.id == id }

val user = findUser(123)
val name = user?.name ?: "Unknown"

// Новый подход с Arrow
fun findUser(id: Int): Option<User> = Option.fromNullable(users.find { it.id == id })

val user = findUser(123)
val name = user.map { it.name }.getOrElse { "Unknown" }
```

### **From CompletableFuture** to `IO`
```kotlin
// Старый подход
fun asyncOperation(): CompletableFuture<String> =
    CompletableFuture.supplyAsync {
        // async operation
        "result"
    }

// Новый подход с Arrow
fun asyncOperation(): IO<String> = IO {
    // async operation
    "result"
}
```

## Решение проблем

### **Common Issues**
```kotlin
// Проблема: Stack overflow в recursion
// Решение: Использовать Eval.defer для stack-safe recursion
fun safeRecursion(n: Int): Eval<Int> =
    if (n <= 0) Eval.now(0)
    else Eval.defer { safeRecursion(n - 1).map { it + 1 } }

// Проблема: Memory leaks с lazy values
// Решение: Использовать Eval.later вместо lazy delegation
val config: Eval<Config> = Eval.later { loadConfig() }

// Проблема: Performance issues с many Either operations
// Решение: Использовать smart constructors и avoid unnecessary boxing
val result = either.eager<String, Int> {
    val a = riskyOp1().bind()
    val b = riskyOp2().bind()
    a + b
}
```

### **Debugging Arrow Code**
```kotlin
// Логирование для Either
fun <A, B> Either<A, B>.log(): Either<A, B> = this.also {
    it.fold(
        { error -> logger.error("Operation failed: $error") },
        { value -> logger.info("Operation succeeded: $value") }
    )
}

// Логирование для Option
fun <A> Option<A>.log(): Option<A> = this.also {
    it.fold(
        { logger.warn("Value is None") },
        { value -> logger.info("Value is Some: $value") }
    )
}

// Трассировка для Try
fun <A> Try<A>.trace(): Try<A> = this.also {
    it.fold(
        { error -> logger.error("Try failed", error) },
        { value -> logger.debug("Try succeeded: $value") }
    )
}
```

## **Experimental Features**

### **Arrow Meta** (**compile-time**)
```kotlin
// Compile-time code generation (экспериментально)
// @optics аннотация генерирует линзы автоматически
@optics
data class Company(val name: String, val employees: List<Employee>) {
    companion object
}

@optics
data class Employee(val name: String, val salary: Double) {
    companion object
}

// Генерируется автоматически:
// val Company.employees: Lens<Company, List<Employee>>
// val Employee.salary: Lens<Employee, Double>
// И многое другое...
```

### **Arrow** Fx **Toolkit**
```kotlin
// Продвинутые операторы для функционального программирования
import arrow.fx.*

// Time-based operations
val delayed = IO.sleep(1.second) *> IO { "Hello after delay" }

// Retry logic
val retrying = IO { riskyOperation() }.retry(Schedule.recurs(3))

// Circuit breaker
val circuitBreaker = CircuitBreaker.of(
    maxFailures = 5,
    resetTimeout = 10.seconds
)

val protected = circuitBreaker.protect(IO { callExternalService() })

// Rate limiting
val rateLimited = IO { processRequest() }
    .rateLimit(tokens = 10, period = 1.second)
```


## Полезные ссылки
- [Официальная документация `Arrow`](https://arrow-kt.io/)
- [Arrow GitHub](https://github.com/arrow-kt/arrow)
- [Arrow Core](https://arrow-kt.io/docs/core/)
- [Arrow Fx](https://arrow-kt.io/docs/effects/fx/)
- [Arrow Optics](https://arrow-kt.io/docs/optics/)

## См. также
- [[kotlin-kotlinx-coroutines|Kotlin Coroutines]] — Асинхронное программирование в **Kotlin**
- [[kotlin-fp-basics|Functional Programming]] — Функциональное программирование в **Kotlin**
- [Паттерны проектирования](../../patterns/README.md) — Паттерны для обработки ошибок

