---
title: "kotlinx.coroutines"
description: "kotlinx.coroutines - это библиотека для асинхронного и конкурентного программирования в Kotlin. Предоставляет мощные примитивы для работы с корутинами, каналами, потоками и асинхронными операциями."
tags:
  - libraries
  - kotlin
  - kotlin-kotlinx-coroutines
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# kotlinx.coroutines

**kotlinx.coroutines** — это библиотека для асинхронного и конкурентного программирования в **Kotlin**. Предоставляет мощные примитивы для работы с корутинами, каналами, потоками и асинхронными операциями.

## Полезные ссылки

### Официальная документация
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) — официальная документация
- [kotlinx.coroutines GitHub](https://github.com/Kotlin/kotlinx.coroutines) — репозиторий проекта
- [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html) — руководство по корутинам

### См. также
- [[kotlin-concurrency-advanced|Kotlin Concurrency]] — **Kotlin Coroutines** в языке
- [[kotlin-arrow|Arrow]] — **Arrow** для функционального программирования

## Содержание

- [Основные возможности](#основные-возможности)
  - [Создание и запуск корутин](#создание-и-запуск-корутин)
  - [Coroutine Builders](#coroutine-builders)
  - [Job и управление жизненным циклом](#job-и-управление-жизненным-циклом)
- [Продвинутые возможности](#продвинутые-возможности)
  - [Flow (**реактивные потоки**)](#flow-реактивные-потоки)
  - [Channel (**коммуникация между корутинами**)](#channel-коммуникация-между-корутинами)
  - [Exception Handling](#exception-handling)
- [Context и Dispatchers](#context-и-dispatchers)
  - [Dispatchers](#dispatchers)
  - [Context manipulation](#context-manipulation)
- [Select Expression](#select-expression)
  - [Channel selection](#channel-selection)
  - [Complex select expressions](#complex-select-expressions)
- [Shared Mutable State](#shared-mutable-state)
  - [Mutex (**взаимное исключение**)](#mutex-взаимное-исключение)
  - [Semaphore](#semaphore)
  - [Actor pattern](#actor-pattern)
- [Testing с coroutines](#testing-с-coroutines)
  - [Testing suspend functions](#testing-suspend-functions)
  - [Testing Flow](#testing-flow)
  - [Mocking coroutines](#mocking-coroutines)
- [Spring Boot Integration](#spring-boot-integration)
  - [Service Layer с coroutines](#service-layer-с-coroutines)
  - [Controller с coroutines](#controller-с-coroutines)
  - [Repository с coroutines](#repository-с-coroutines)
- [Reactive Programming Integration](#reactive-programming-integration)
  - [Flow с Reactor](#flow-с-reactor)
  - [RxJava Integration](#rxjava-integration)
- [Performance Optimization](#performance-optimization)
  - [Structured Concurrency](#structured-concurrency)
  - [Resource Management](#resource-management)
  - [Memory-efficient Flow Processing](#memory-efficient-flow-processing)
- [Best Practices](#best-practices)
  - [Error Handling Patterns](#error-handling-patterns)
  - [Cancellation Best Practices](#cancellation-best-practices)
  - [Testing Patterns](#testing-patterns)
- [Debugging и Troubleshooting](#debugging-и-troubleshooting)
  - [Debugging Coroutines](#debugging-coroutines)
  - [Common Issues](#common-issues)
- [Migration Guide](#migration-guide)
  - [From Callbacks to Coroutines](#from-callbacks-to-coroutines)
  - [From RxJava to Coroutines Flow](#from-rxjava-to-coroutines-flow)
  - [From Threading to Coroutines](#from-threading-to-coroutines)
- [Experimental Features](#experimental-features)
  - [Kotlin 1.7+ Features](#kotlin-17-features)
  - [SharedFlow и StateFlow](#sharedflow-и-stateflow)

## Основные возможности

### Создание и запуск корутин

Пример создания и запуска корутин в **kotlinx-coroutines** (**Kotlin**).

```kotlin
import kotlinx.coroutines.*

/
 * Демонстрация создания и запуска корутин в Kotlin
 * Корутины - это легковесные потоки для асинхронного программирования
 */
fun main() = runBlocking {
    // runBlocking - блокирующий builder корутин
    // Блокирует текущий поток до завершения всех корутин внутри блока
    // Используется в main() или для тестирования, в продакшене предпочтительнее coroutineScope

    // launch - builder для "fire and forget" корутин
    // Запускает корутину и не ждет результата
    launch {
        // Асинхронная операция - выполняется в фоне
        delay(1000L)  // delay - suspend функция, приостанавливает корутину на 1 секунду
        // Во время delay корутина освобождает поток, позволяя другим корутинам выполняться
        println("World!")
    }

    // Этот код выполнится сразу, не дожидаясь завершения launch
    println("Hello,")
    // Вывод: Hello, World! (после задержки в 1 секунду)
}

/
 * Async/await паттерн для получения результатов из корутин
 * async возвращает Deferred<T> - аналог Future/Promise
 */
fun main() = runBlocking {
    // async - builder для корутин с возвращаемым значением
    // Возвращает Deferred<T> - отложенное значение
    val deferred = async {
        // Асинхронная операция, которая возвращает результат
        delay(1000L)  // Симуляция долгой операции
        "Result"  // Возвращаемое значение
    }

    // Код продолжает выполняться, не дожидаясь результата
    println("Waiting...")

    // await() - приостанавливает корутину до получения результата
    // Если результат уже готов, возвращает его сразу
    // Если нет - приостанавливает корутину, освобождая поток
    val result = deferred.await()
    println("Got: $result")  // Вывод: Got: Result
}
```

### **Coroutine Builders**
```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.produce

/
 * Различные builders для создания корутин
 * Каждый builder имеет свое назначение и возвращаемый тип
 */
suspend fun main() = coroutineScope {
    // coroutineScope - создает область видимости для корутин
    // Ждет завершения всех дочерних корутин перед завершением
    // Не блокирует поток (в отличие от runBlocking)

    // launch - запускает корутину без возвращаемого значения
    // Возвращает Job - для управления жизненным циклом корутины
    // "Fire and forget" паттерн - запустили и забыли
    launch {
        println("Launch: Start")  // Выполнится сразу
        delay(500)  // Приостановка на 500мс
        println("Launch: End")    // Выполнится через 500мс
    }

    // async - запускает корутину с возвращаемым значением
    // Возвращает Deferred<T> - отложенное значение
    // Позволяет получить результат асинхронной операции
    val deferred = async {
        println("Async: Start")  // Выполнится сразу
        delay(1000)  // Приостановка на 1 секунду
        "Async Result"  // Возвращаемое значение
    }

    // produce - создает корутину-производитель данных
    // Возвращает ReceiveChannel<T> - канал для получения данных
    // Автоматически закрывает канал при завершении корутины
    val channel = produce {
        // Генерация последовательности значений
        for (i in 1..5) {
            send(i)  // Отправка значения в канал
            delay(100)  // Задержка между отправками
        }
        // Канал автоматически закроется после цикла
    }

    // Запуск и ожидание результата от async
    val result = deferred.await()  // Приостановится до получения результата
    println("Result: $result")  // Вывод: Result: Async Result

    // Чтение из канала (consumer)
    // Итерация по каналу автоматически приостанавливается при отсутствии данных
    for (value in channel) {
        // value будет получен когда производитель отправит его
        println("Received: $value")  // Вывод: Received: 1, 2, 3, 4, 5
    }
    // После завершения цикла канал автоматически закрыт
}
```

### **Job** и управление жизненным циклом
```kotlin
import kotlinx.coroutines.*

/
 * Управление жизненным циклом корутин через Job
 * Job позволяет отменять, отслеживать состояние и ждать завершения корутин
 */
fun main() = runBlocking {
    // launch возвращает Job - объект для управления корутиной
    val job = launch {
        // Повторение операции 1000 раз
        repeat(1000) { i ->
            println("job: I'm sleeping $i ...")
            delay(500L)  // Приостановка на 500мс
            // delay() проверяет отмену корутины и выбрасывает CancellationException при отмене
        }
    }

    // Задержка на 1.3 секунды - корутина успеет выполниться 2-3 раза
    delay(1300L)
    println("main: I'm tired of waiting!")

    // cancel() - отменяет корутину
    // Устанавливает флаг отмены, который проверяется в suspend функциях
    // Если корутина в suspend функции (delay), она получит CancellationException
    job.cancel()

    // join() - приостанавливает текущую корутину до завершения job
    // После отмены job завершится быстро (выбросит CancellationException)
    job.join()

    println("main: Now I can quit.")  // Выполнится после завершения job
}
```

## Продвинутые возможности

### **Flow** (**реактивные потоки**)
```kotlin
import kotlinx.coroutines.flow.*

/
 * Flow - реактивный поток данных для корутин
 * Аналог RxJava Observable или Reactor Flux
 * Позволяет обрабатывать последовательности данных асинхронно
 */
fun simpleFlow(): Flow<Int> = flow {
    // flow { } - builder для создания Flow
    // Внутри можно использовать suspend функции
    for (i in 1..3) {
        delay(100)  // Приостановка между эмиссиями
        emit(i)  // emit() - отправка значения в поток
        // Каждое значение будет обработано коллектором последовательно
    }
    // Flow завершится после завершения блока
}

suspend fun main() {
    // Создание Flow - холодный поток (cold stream)
    // Выполнение начинается только при вызове терминального оператора
    val flow = simpleFlow()

    // toList() - терминальный оператор, собирает все значения в список
    // Приостанавливает корутину до получения всех значений
    val result = flow.toList()
    println(result)  // [1, 2, 3]

    // collect {} - терминальный оператор для обработки каждого значения
    // Приостанавливается для каждого значения, обрабатывает их последовательно
    flow.collect { value ->
        println("Collected: $value")  // Вывод: Collected: 1, 2, 3
    }

    // Промежуточные операторы - создают новый Flow без выполнения
    // Преобразования применяются лениво при вызове терминального оператора
    val doubled = flow.map { it * 2 }  // Умножает каждое значение на 2
    val filtered = flow.filter { it % 2 == 0 }  // Фильтрует четные числа

    // fold() - терминальный оператор для агрегации
    // Накапливает значения используя начальное значение и функцию
    val sum = flow.fold(0) { acc, value -> acc + value }  // Суммирует все значения
    // acc - аккумулятор (начальное значение 0)
    // value - текущее значение из потока
    // Результат: 0 + 1 + 2 + 3 = 6

    println("Sum: $sum")  // 6
}

/
 * Продвинутые Flow операции с цепочкой преобразований
 */
fun advancedFlow(): Flow<String> = flow {
    // Генерация значений
    emit("A")  // Отправка "A"
    emit("B")  // Отправка "B"
    delay(1000)  // Задержка 1 секунда
    emit("C")  // Отправка "C"
}
.map { it.lowercase() }  // Преобразование: "A" -> "a", "B" -> "b", "C" -> "c"
.filter { it != "b" }  // Фильтрация: убираем "b", остаются "a" и "c"
.transform { value ->
    // transform - кастомное преобразование с возможностью эмитировать несколько значений
    emit("Before $value")  // Эмитим "Before a"
    emit(value)  // Эмитим само значение "a"
    emit("After $value")  // Эмитим "After a"
    // Для каждого значения из предыдущего Flow создаем 3 значения
}
// Итоговый Flow: "Before a", "a", "After a", "Before c", "c", "After c"
```

### **Channel** (**коммуникация между корутинами**)
```kotlin
import kotlinx.coroutines.channels.*

suspend fun main() = coroutineScope {
    // Создание канала
    val channel = Channel<Int>()

    // Producer
    launch {
        for (x in 1..5) {
            channel.send(x * x)
            delay(100)
        }
        channel.close() // Закрытие канала
    }

    // Consumer
    launch {
        for (y in channel) {
            println("Received: $y")
        }
        println("Done!")
    }
}

// Buffered channels
suspend fun bufferedChannel() = coroutineScope {
    val channel = Channel<Int>(capacity = 3) // Буфер на 3 элемента

    launch {
        repeat(10) {
            channel.send(it)
            println("Sent: $it")
        }
        channel.close()
    }

    launch {
        for (received in channel) {
            println("Received: $received")
            delay(500) // Имитация обработки
        }
    }
}
```

### **Exception Handling**
```kotlin
import kotlinx.coroutines.*

suspend fun main() = coroutineScope {
    // Обработка исключений в корутинах
    val job = launch {
        try {
            riskyOperation()
        } catch (e: Exception) {
            println("Caught exception: ${e.message}")
        }
    }

    job.join()
}

suspend fun riskyOperation() {
    delay(100)
    throw RuntimeException("Something went wrong!")
}

// SupervisorJob - изоляция ошибок
suspend fun supervisorExample() = supervisorScope {
    val child1 = launch {
        delay(100)
        throw RuntimeException("Child 1 failed")
    }

    val child2 = launch {
        delay(200)
        println("Child 2 completed successfully")
    }

    // Child1 завершится с ошибкой, но child2 продолжит работу
}

// Cancellation и cleanup
suspend fun cleanupExample() = coroutineScope {
    val job = launch {
        try {
            repeat(1000) { i ->
                println("Processing $i")
                delay(100)
            }
        } finally {
            println("Cleanup: closing resources")
            // Здесь происходит cleanup
        }
    }

    delay(500)
    job.cancel()
    job.join()
}
```

## **Context** и **Dispatchers**

### **Dispatchers**
```kotlin
import kotlinx.coroutines.*

suspend fun main() = coroutineScope {
    // Default dispatcher (для CPU-bound задач)
    launch(Dispatchers.Default) {
        println("Default: ${Thread.currentThread().name}")
        heavyComputation()
    }

    // IO dispatcher (для IO операций)
    launch(Dispatchers.IO) {
        println("IO: ${Thread.currentThread().name}")
        networkCall()
    }

    // Main dispatcher (для UI операций на Android)
    // launch(Dispatchers.Main) { ... }

    // Unconfined dispatcher
    launch(Dispatchers.Unconfined) {
        println("Unconfined: ${Thread.currentThread().name}")
        delay(100)
        println("Unconfined after delay: ${Thread.currentThread().name}")
    }
}

suspend fun heavyComputation() {
    // CPU intensive work
    (1..100000).sum()
}

suspend fun networkCall() {
    delay(1000) // IO simulation
    println("Network call completed")
}
```

### **Context manipulation**
```kotlin
import kotlinx.coroutines.*

suspend fun main() = withContext(Dispatchers.Default) {
    val userId = 123

    // Смена контекста для IO операции
    val userData = withContext(Dispatchers.IO) {
        loadUserData(userId)
    }

    // Обработка данных в Default контексте
    val processedData = processData(userData)

    // Сохранение результата в IO контексте
    withContext(Dispatchers.IO) {
        saveProcessedData(processedData)
    }
}

suspend fun loadUserData(userId: Int): String {
    delay(100) // IO simulation
    return "User data for $userId"
}

fun processData(data: String): String = data.uppercase()

suspend fun saveProcessedData(data: String) {
    delay(50) // IO simulation
    println("Saved: $data")
}
```

## **Select Expression**

### **Channel selection**
```kotlin
import kotlinx.coroutines.selects.*

suspend fun selectExample() = coroutineScope {
    val channel1 = Channel<String>()
    val channel2 = Channel<String>()

    // Producer 1
    launch {
        delay(100)
        channel1.send("From channel 1")
    }

    // Producer 2
    launch {
        delay(200)
        channel2.send("From channel 2")
    }

    // Select - ждет первого доступного результата
    val result = select<String> {
        channel1.onReceive { "Channel1: $it" }
        channel2.onReceive { "Channel2: $it" }
    }

    println(result) // Channel1: From channel 1 (придет первым)
}
```

### **Complex select expressions**
```kotlin
suspend fun complexSelect() = coroutineScope {
    val channels = List(3) { Channel<Int>() }

    // Producers
    channels.forEachIndexed { index, channel ->
        launch {
            delay((index + 1) * 100L)
            channel.send(index + 1)
        }
    }

    // Select с различными условиями
    repeat(3) {
        val result = select<String> {
            channels.forEachIndexed { index, channel ->
                channel.onReceive { value ->
                    "Channel ${index + 1} sent: $value"
                }
            }
        }
        println(result)
    }
}
```

## **Shared Mutable State**

### **Mutex** (**взаимное исключение**)
```kotlin
import kotlinx.coroutines.sync.*

suspend fun main() = coroutineScope {
    val mutex = Mutex()
    var counter = 0

    val jobs = List(100) {
        launch {
            repeat(1000) {
                mutex.withLock {
                    counter++
                }
            }
        }
    }

    jobs.forEach { it.join() }
    println("Counter: $counter") // 100000
}
```

### **Semaphore**
```kotlin
import kotlinx.coroutines.sync.*

suspend fun main() = coroutineScope {
    val semaphore = Semaphore(2) // Разрешает максимум 2 одновременных операции

    val jobs = List(10) {
        launch {
            semaphore.acquire()
            try {
                println("Processing with permit")
                delay(1000)
                println("Processing completed")
            } finally {
                semaphore.release()
            }
        }
    }

    jobs.forEach { it.join() }
}
```

### **Actor pattern**
```kotlin
import kotlinx.coroutines.channels.*

sealed class CounterMsg
object IncCounter : CounterMsg()
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg()

fun CoroutineScope.counterActor() = actor<CounterMsg> {
    var counter = 0

    for (msg in channel) {
        when (msg) {
            is IncCounter -> counter++
            is GetCounter -> msg.response.complete(counter)
        }
    }
}

suspend fun main() = coroutineScope {
    val actor = counterActor()

    // Отправка сообщений
    repeat(10) {
        actor.send(IncCounter)
    }

    // Получение значения
    val response = CompletableDeferred<Int>()
    actor.send(GetCounter(response))

    println("Counter value: ${response.await()}") // 10
    actor.close()
}
```

## **Testing** с **coroutines**

### **Testing suspend functions**
```kotlin
import kotlinx.coroutines.test.*
import kotlin.test.*

class UserServiceTest {

    @Test
    fun `should load user successfully`() = runTest {
        val userService = UserService()

        val user = userService.loadUser(123)

        assertEquals(123, user.id)
        assertEquals("John Doe", user.name)
    }

    @Test
    fun `should handle timeout correctly`() = runTest {
        val userService = UserService()

        // Установка виртуального времени
        advanceTimeBy(6000) // Пропуск 6 секунд

        assertFailsWith<TimeoutCancellationException> {
            userService.loadUserWithTimeout(123)
        }
    }
}
```

### **Testing Flow**
```kotlin
import app.cash.turbine.test

class DataRepositoryTest {

    @Test
    fun `should emit data correctly`() = runTest {
        val repository = DataRepository()

        repository.getDataFlow().test {
            assertEquals("Loading", awaitItem())
            assertEquals("Data1", awaitItem())
            assertEquals("Data2", awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `should handle errors in flow`() = runTest {
        val repository = DataRepository()

        repository.getErrorFlow().test {
            assertEquals("Loading", awaitItem())
            assertFailsWith<RuntimeException> { awaitItem() }
        }
    }
}
```

### **Mocking coroutines**
```kotlin
import io.mockk.coEvery
import io.mockk.coVerify

class ApiServiceTest {

    private val apiService = mockk<ApiService>()

    @Test
    fun `should call API correctly`() = runTest {
        // Given
        coEvery { apiService.getUser(123) } returns User(123, "John")

        // When
        val result = apiService.getUser(123)

        // Then
        assertEquals("John", result.name)
        coVerify { apiService.getUser(123) }
    }

    @Test
    fun `should handle network errors`() = runTest {
        // Given
        coEvery { apiService.getUser(any()) } throws IOException("Network error")

        // When & Then
        assertFailsWith<IOException> {
            apiService.getUser(123)
        }
    }
}
```

## **Spring Boot Integration**

### **Service Layer** с **coroutines**
```kotlin
@Service
class UserService(
    private val userRepository: UserRepository,
    private val externalApiClient: ExternalApiClient
) {

    suspend fun getUserWithDetails(userId: Long): UserDetails = coroutineScope {
        val userDeferred = async { userRepository.findById(userId) ?: throw UserNotFoundException() }
        val profileDeferred = async { externalApiClient.getUserProfile(userId) }
        val postsDeferred = async { externalApiClient.getUserPosts(userId) }

        val user = userDeferred.await()
        val profile = profileDeferred.await()
        val posts = postsDeferred.await()

        UserDetails(user, profile, posts)
    }

    suspend fun processUsersInParallel(userIds: List<Long>): List<User> = coroutineScope {
        userIds.map { userId ->
            async {
                try {
                    userRepository.findById(userId)
                } catch (e: Exception) {
                    null // Handle errors gracefully
                }
            }
        }.awaitAll().filterNotNull()
    }

    suspend fun createUserWithValidation(request: CreateUserRequest): User = coroutineScope {
        // Параллельная валидация
        val emailCheck = async { validateEmailUniqueness(request.email) }
        val nameCheck = async { validateName(request.name) }

        emailCheck.await()
        nameCheck.await()

        // Создание пользователя
        val user = User(
            name = request.name,
            email = request.email
        )

        userRepository.save(user)
    }

    private suspend fun validateEmailUniqueness(email: String) {
        val existingUser = userRepository.findByEmail(email)
        if (existingUser != null) {
            throw EmailAlreadyExistsException()
        }
    }

    private fun validateName(name: String) {
        if (name.isBlank()) {
            throw InvalidNameException()
        }
    }
}
```

### **Controller** с **coroutines**
```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/{id}")
    suspend fun getUser(@PathVariable id: Long): ResponseEntity<UserDetails> {
        return try {
            val userDetails = userService.getUserWithDetails(id)
            ResponseEntity.ok(userDetails)
        } catch (e: UserNotFoundException) {
            ResponseEntity.notFound().build()
        } catch (e: Exception) {
            ResponseEntity.status(500).build()
        }
    }

    @GetMapping("/batch")
    suspend fun getUsersBatch(@RequestParam ids: List<Long>): List<User> {
        return userService.processUsersInParallel(ids)
    }

    @PostMapping
    suspend fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<UserResponse> {
        return try {
            val user = userService.createUserWithValidation(request)
            ResponseEntity.ok(UserResponse.from(user))
        } catch (e: EmailAlreadyExistsException) {
            ResponseEntity.badRequest().body(
                UserResponse.error("Email already exists")
            )
        } catch (e: InvalidNameException) {
            ResponseEntity.badRequest().body(
                UserResponse.error("Invalid name")
            )
        }
    }

    @GetMapping("/stream")
    fun getUsersStream(): Flow<User> = flow {
        val users = userService.getAllUsers()
        users.forEach { user ->
            emit(user)
            delay(100) // Имитация паузы между элементами
        }
    }
}
```

### **Repository** с **coroutines**
```kotlin
@Repository
class UserRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : UserRepository {

    suspend fun findById(id: Long): User? = withContext(Dispatchers.IO) {
        jdbcTemplate.query(
            "SELECT * FROM users WHERE id = ?",
            { rs, _ ->
                User(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    email = rs.getString("email")
                )
            },
            id
        ).firstOrNull()
    }

    suspend fun findByEmail(email: String): User? = withContext(Dispatchers.IO) {
        jdbcTemplate.query(
            "SELECT * FROM users WHERE email = ?",
            { rs, _ ->
                User(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    email = rs.getString("email")
                )
            },
            email
        ).firstOrNull()
    }

    suspend fun save(user: User): User = withContext(Dispatchers.IO) {
        val savedUser = if (user.id == 0L) {
            // Insert
            val id = jdbcTemplate.queryForObject(
                "INSERT INTO users (name, email) VALUES (?, ?) RETURNING id",
                Long::class.java,
                user.name, user.email
            )!!
            user.copy(id = id)
        } else {
            // Update
            jdbcTemplate.update(
                "UPDATE users SET name = ?, email = ? WHERE id = ?",
                user.name, user.email, user.id
            )
            user
        }
        savedUser
    }

    suspend fun findAll(): List<User> = withContext(Dispatchers.IO) {
        jdbcTemplate.query("SELECT * FROM users") { rs, _ ->
            User(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                email = rs.getString("email")
            )
        }
    }
}
```

## **Reactive Programming Integration**

### **Flow** с **Reactor**
```kotlin
import reactor.core.publisher.*

fun flowToMono(flow: Flow<String>): Mono<String> = flow.asMono()

fun flowToFlux(flow: Flow<String>): Flux<String> = flow.asFlux()

suspend fun reactorExample() {
    val flow = flowOf("A", "B", "C")
        .map { it.lowercase() }
        .filter { it != "b" }

    // Конвертация в Reactor types
    val mono = flowToMono(flow)
    val flux = flowToFlux(flow)

    // Использование Reactor operators
    val result = flux
        .map { "Item: $it" }
        .collectList()
        .awaitSingle()
}
```

### **RxJava Integration**
```kotlin
import io.reactivex.rxjava3.core.*

fun flowToObservable(flow: Flow<String>): Observable<String> {
    return Observable.create { emitter ->
        flow.collect { value ->
            emitter.onNext(value)
        }
        emitter.onComplete()
    }
}

suspend fun rxJavaExample() {
    val flow = flowOf("A", "B", "C")

    val observable = flowToObservable(flow)

    val result = observable
        .map { it.lowercase() }
        .filter { it != "b" }
        .toList()
        .blockingGet()
}
```

## **Performance Optimization**

### **Structured Concurrency**
```kotlin
suspend fun processBatch(items: List<Item>) = coroutineScope {
    // Все дочерние корутины завершаются при завершении scope
    items.map { item ->
        async {
            processItem(item)
        }
    }.awaitAll()
}

suspend fun safeBatchProcessing(items: List<Item>) = supervisorScope {
    // Ошибки в дочерних корутинах не влияют на другие
    items.map { item ->
        async {
            try {
                processItem(item)
            } catch (e: Exception) {
                log.error("Failed to process item", e)
                null // Return null for failed items
            }
        }
    }.awaitAll().filterNotNull()
}
```

### **Resource Management**
```kotlin
class DatabaseConnection : AutoCloseable {
    suspend fun connect() { /* ... */ }
    suspend fun execute(query: String): ResultSet { /* ... */ }
    override fun close() { /* ... */ }
}

suspend fun useDatabase(): List<User> {
    return DatabaseConnection().use { connection ->
        connection.connect()

        coroutineScope {
            val usersQuery = async { connection.execute("SELECT * FROM users") }
            val profilesQuery = async { connection.execute("SELECT * FROM profiles") }

            val users = usersQuery.await()
            val profiles = profilesQuery.await()

            // Combine results
            combineUserData(users, profiles)
        }
    }
}
```

### **Memory-efficient Flow Processing**
```kotlin
fun processLargeFile(file: File): Flow<String> = flow {
    file.useLines { lines ->
        lines.forEach { line ->
            emit(line)
            yield() // Позволяет другим корутинам работать
        }
    }
}

suspend fun efficientProcessing() {
    val file = File("large-dataset.txt")

    processLargeFile(file)
        .buffer(100) // Буфер для batch обработки
        .map { line -> processLine(line) }
        .filter { it.isValid }
        .collect { result ->
            saveToDatabase(result)
        }
}
```

## Лучшие практики

### **Error Handling Patterns**
```kotlin
// Result pattern with coroutines
sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val error: Throwable) : Result<Nothing>()
}

suspend fun <T> safeCall(block: suspend () -> T): Result<T> = try {
    Result.Success(block())
} catch (e: Exception) {
    Result.Failure(e)
}

// Usage
suspend fun apiCall(): Result<User> = safeCall {
    userApi.getUser(123)
}

suspend fun handleResult() {
    when (val result = apiCall()) {
        is Result.Success -> println("User: ${result.value.name}")
        is Result.Failure -> println("Error: ${result.error.message}")
    }
}
```

### **Cancellation Best Practices**
```kotlin
class CancellableService {

    suspend fun longRunningOperation(progressCallback: (Int) -> Unit) = coroutineScope {
        val job = launch {
            for (i in 1..100) {
                ensureActive() // Проверка на cancellation
                progressCallback(i)
                delay(100)
            }
        }

        job.invokeOnCompletion { cause ->
            if (cause is CancellationException) {
                println("Operation was cancelled")
            }
        }

        job.join()
    }

    suspend fun cancellableNetworkCall(): String = withTimeout(5000) {
        networkCall() // Автоматическая отмена через 5 секунд
    }
}
```

### **Testing Patterns**
```kotlin
class CoroutineTestingPatterns {

    @Test
    fun `should handle timeout correctly`() = runTest {
        val service = TimeSensitiveService()

        // Test timeout behavior
        assertFailsWith<TimeoutCancellationException> {
            withTimeout(100) {
                service.longOperation()
            }
        }
    }

    @Test
    fun `should process items in parallel`() = runTest {
        val processor = ParallelProcessor()

        val items = List(10) { "Item$it" }
        val results = processor.processParallel(items)

        assertEquals(10, results.size)
        assertTrue(results.all { it.startsWith("Processed: ") })
    }

    @Test
    fun `should handle cancellation gracefully`() = runTest {
        val service = CancellableService()

        val job = launch {
            service.longRunningOperation { progress ->
                if (progress >= 50) {
                    cancel() // Cancel in the middle
                }
            }
        }

        job.join()
        assertTrue(job.isCancelled)
    }
}
```

## **Debugging** и **Troubleshooting**

### **Debugging Coroutines**
```kotlin
suspend fun debugCoroutines() = coroutineScope {
    val job = launch(CoroutineName("DebugJob")) {
        println("Coroutine name: ${coroutineContext[CoroutineName]?.name}")
        println("Coroutine id: ${coroutineContext[Job]?.toString()}")

        delay(1000)
        println("After delay")
    }

    // Мониторинг состояния
    launch {
        while (job.isActive) {
            println("Job state: ${job.toString()}")
            delay(200)
        }
        println("Final job state: ${job.toString()}")
    }

    job.join()
}

// Exception handling
suspend fun robustCoroutine() {
    val handler = CoroutineExceptionHandler { context, exception ->
        println("Caught exception: $exception")
        println("In coroutine: ${context[CoroutineName]?.name}")
    }

    coroutineScope {
        launch(handler) {
            throw RuntimeException("Test exception")
        }
    }
}
```

### **Common Issues**
```kotlin
object CoroutineTroubleshooting {

    // Проблема: Leaking coroutines
    suspend fun properScope() = coroutineScope {
        // Все дочерние корутины автоматически отменяются
        launch { /* work */ }
        async { /* work */ }
        // ...
    }

    // Проблема: Blocking main thread
    suspend fun nonBlocking() = withContext(Dispatchers.IO) {
        // IO операции не блокируют main thread
        fileOperation()
    }

    // Проблема: Race conditions
    suspend fun atomicOperation() = mutex.withLock {
        // Thread-safe operation
        counter++
    }

    // Проблема: Memory leaks with Flow
    fun memorySafeFlow(): Flow<Int> = flow {
        val items = loadLargeDataset() // Большой датасет
        try {
            items.forEach { emit(it) }
        } finally {
            items.clear() // Очистка памяти
        }
    }
}
```

## **Migration Guide**

### **From Callbacks** to **Coroutines**
```kotlin
// Callback-based code
fun fetchUser(id: Int, callback: (User?, Throwable?) -> Unit) {
    // Async operation
}

// Coroutine version
suspend fun fetchUser(id: Int): User = suspendCoroutine { continuation ->
    fetchUser(id) { user, error ->
        if (error != null) {
            continuation.resumeWithException(error)
        } else {
            continuation.resume(user!!)
        }
    }
}

// Usage
suspend fun loadUser() {
    try {
        val user = fetchUser(123)
        println("User: $user")
    } catch (e: Exception) {
        println("Error: $e")
    }
}
```

### **From RxJava** to **Coroutines Flow**
```kotlin
// RxJava
fun getUsers(): Observable<User> = userApi.getUsers()
    .map { it.toDomain() }
    .filter { it.active }
    .subscribeOn(Schedulers.io())

// Coroutines Flow
fun getUsers(): Flow<User> = flow {
    userApi.getUsers()
        .map { it.toDomain() }
        .filter { it.active }
        .forEach { emit(it) }
}.flowOn(Dispatchers.IO)

// Usage
suspend fun collectUsers() {
    getUsers()
        .collect { user ->
            println("User: $user")
        }
}
```

### **From Threading** to **Coroutines**
```kotlin
// Thread-based code
fun processInBackground(callback: (Result) -> Unit) {
    thread {
        val result = heavyComputation()
        callback(result)
    }
}

// Coroutine version
suspend fun processAsync(): Result = withContext(Dispatchers.Default) {
    heavyComputation()
}

// Usage
suspend fun main() {
    val result = processAsync()
    println("Result: $result")
}
```

## **Experimental Features**

### **Kotlin** 1.7+ **Features**
```kotlin
// Auto-closeable coroutines (предполагаемый API)
suspend fun useResource(): String = autoCloseableResource { resource ->
    resource.use { it.readData() }
}

// Native coroutine cancellation
suspend fun cancellableOperation() = withCancellable { cancellationToken ->
    while (!cancellationToken.isCancelled) {
        // Operation that checks for cancellation
        performStep()
    }
}

// Improved Flow operators
suspend fun advancedFlowOperations() {
    val flow = flowOf(1, 2, 3, 4, 5)
        .runningFold(0) { acc, value -> acc + value } // [1, 3, 6, 10, 15]
        .runningReduce { acc, value -> acc * value }  // [2, 6, 24, 120]
        .chunked(2) // [[1,2], [3,4], [5]]
        .flatten()
}
```

### **SharedFlow** и **StateFlow**
```kotlin
// SharedFlow для broadcasting
class EventBus {
    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events

    suspend fun postEvent(event: Event) {
        _events.emit(event)
    }
}

// StateFlow для state management
class UserPreferences {
    private val _theme = MutableStateFlow(Theme.LIGHT)
    val theme: StateFlow<Theme> = _theme

    suspend fun setTheme(theme: Theme) {
        _theme.emit(theme)
    }
}
```


## Полезные ссылки
- [Официальная документация `kotlinx.coroutines`](https://github.com/Kotlin/kotlinx.coroutines)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Flow Documentation](https://kotlinlang.org/docs/flow.html)
- [Channels Guide](https://kotlinlang.org/docs/channels.html)

## См. также
- [[kotlin-basics|Kotlin Basics]] — Основы **Kotlin**
- [Паттерны](../../patterns/README.md) — Реактивные паттерны
- [[java-concurrency-basics|Java Concurrency]] — Асинхронное программирование в **Java**

