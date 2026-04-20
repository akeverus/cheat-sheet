---
title: "Kotlin Testing"
description: "Кратко: полное руководство по тестированию Kotlin кода. Рассматриваются JUnit 5, MockK, Kotlin Test Framework, тестирование корутин, property-based testing и лучшие практики."
tags:
  - languages
  - kotlin
  - kotlin-testing
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kotlin Testing

Кратко: полное руководство по тестированию **Kotlin** кода. Рассматриваются **JUnit** 5, **MockK**, **Kotlin Test Framework**, тестирование корутин, **property-based testing** и лучшие практики.

## Полезные ссылки

### Официальная документация
- [Kotlin Testing](https://kotlinlang.org/docs/jvm-test-using-junit.html)
- [MockK Documentation](https://mockk.io/)

### Обучающие материалы
- [Kotlin Testing Tutorial](https://www.baeldung.com/kotlin/junit-5-kotlin)

### См. также
- [[kotlin-basics|Основы Kotlin]]
- [[kotlin-concurrency-basics|Корутины]]
- [[kotlin-another|Общее тестирование]]

- [[kotlin-performance|Kotlin Performance]]
- [[kotlin-fp-basics|Kotlin Functional Programming: Basics]]
## Содержание

- [Введение в тестирование Kotlin](#введение-в-тестирование-kotlin)
  - [Особенности тестирования Kotlin](#особенности-тестирования-kotlin)
  - [Структура тестов](#структура-тестов)
- [JUnit 5 с Kotlin](#junit-5-с-kotlin)
  - [Базовые тесты](#базовые-тесты)
  - [Параметризованные тесты](#параметризованные-тесты)
  - [Lifecycle методы](#lifecycle-методы)
- [MockK](#mockk)
  - [Базовое мокирование](#базовое-мокирование)
  - [Мокирование suspend функций](#мокирование-suspend-функций)
  - [Relaxed моки](#relaxed-моки)
  - [Захват аргументов](#захват-аргументов)
- [Kotlin Test Framework](#kotlin-test-framework)
  - [Assertions](#assertions)
  - [Matchers](#matchers)
- [Тестирование корутин](#тестирование-корутин)
  - [runTest](#runtest)
  - [Тестирование Flow](#тестирование-flow)
  - [Тестирование suspend функций](#тестирование-suspend-функций)
- [Property-Based Testing](#property-based-testing)
  - [KotlinTest Property Testing](#kotlintest-property-testing)
- [Интеграционное тестирование](#интеграционное-тестирование)
  - [Spring Boot тесты](#spring-boot-тесты)
  - [Тестирование с базой данных](#тестирование-с-базой-данных)
- [Лучшие практики](#лучшие-практики)
  - [Именование тестов](#именование-тестов)
  - [Один assertion на тест](#один-assertion-на-тест)
  - [Использование data классов](#использование-data-классов)
  - [Тестирование с использованием builders](#тестирование-с-использованием-builders)
- [Тестирование корутин](#тестирование-корутин-1)
  - [Использование runTest](#использование-runtest)
  - [Тестирование Flow](#тестирование-flow-1)
  - [Мокирование suspend функций](#мокирование-suspend-функций-1)
- [Тестирование исключений](#тестирование-исключений)
  - [Использование assertThrows](#использование-assertthrows)
  - [Использование shouldThrow в KotlinTest](#использование-shouldthrow-в-kotlintest)
- [Параметризованные тесты](#параметризованные-тесты-1)
  - [JUnit 5 параметризованные тесты](#junit-5-параметризованные-тесты)
  - [KotlinTest табличное тестирование](#kotlintest-табличное-тестирование)
- [Тестирование приватных методов](#тестирование-приватных-методов)
  - [Использование @VisibleForTesting](#использование-visiblefortesting)
  - [Рефлексия для тестирования](#рефлексия-для-тестирования)
- [Тестирование с зависимостями](#тестирование-с-зависимостями)
  - [Использование dependency injection](#использование-dependency-injection)
- [Тестирование производительности](#тестирование-производительности)
  - [Использование @Timeout](#использование-timeout)
  - [Бенчмаркинг в тестах](#бенчмаркинг-в-тестах)
- [Организация тестов](#организация-тестов)
  - [Структура тестовых классов](#структура-тестовых-классов)
  - [Использование @BeforeEach и @AfterEach](#использование-beforeeach-и-aftereach)
- [Покрытие кода](#покрытие-кода)
  - [Настройка JaCoCo](#настройка-jacoco)
  - [Целевое покрытие](#целевое-покрытие)
- [Тестирование с моками и стабами](#тестирование-с-моками-и-стабами)
  - [Использование MockK для продвинутых сценариев](#использование-mockk-для-продвинутых-сценариев)
  - [Тестирование с несколькими зависимостями](#тестирование-с-несколькими-зависимостями)
- [Performance тестирование](#performance-тестирование)
  - [Измерение производительности тестов](#измерение-производительности-тестов)
  - [Нагрузочное тестирование](#нагрузочное-тестирование)
- [Продвинутые техники тестирования](#продвинутые-техники-тестирования)
  - [Property-based testing](#property-based-testing-1)
  - [Mutation testing](#mutation-testing)
- [Интеграционное тестирование](#интеграционное-тестирование-1)
  - [Тестирование с базами данных](#тестирование-с-базами-данных)
  - [Тестирование API](#тестирование-api)
- [Дополнительные техники тестирования](#дополнительные-техники-тестирования)
  - [Contract testing](#contract-testing)
  - [Snapshot testing](#snapshot-testing)
- [Дополнительные техники тестирования](#дополнительные-техники-тестирования-1)
  - [Golden File Testing](#golden-file-testing)
  - [Visual Regression Testing](#visual-regression-testing)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Итоговые рекомендации](#итоговые-рекомендации)
- [Практические примеры использования](#практические-примеры-использования)
  - [Тестирование сервиса с зависимостями](#тестирование-сервиса-с-зависимостями)
  - [Интеграционное тестирование API](#интеграционное-тестирование-api)
  - [Тестирование корутин](#тестирование-корутин-2)
  - [Тестирование Flow](#тестирование-flow-2)
  - [Практические примеры: Тестирование с Testcontainers](#практические-примеры-тестирование-с-testcontainers)
  - [Практические примеры: Тестирование с WireMock](#практические-примеры-тестирование-с-wiremock)
  - [Практические примеры: Property-based тестирование](#практические-примеры-property-based-тестирование)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)

## Введение в тестирование Kotlin

Тестирование **Kotlin** кода имеет свои особенности из-за специфики языка: **null safety**, **data** классы, корутины и другие возможности требуют специальных подходов к тестированию.

### Особенности тестирования Kotlin

- **Null Safety**: система типов помогает избежать многих ошибок, но требует тестирования **nullable** типов
- **Data классы**: автоматическая генерация **equals**/**hashCode** упрощает сравнение объектов
- **Корутины**: требуют специальных инструментов для тестирования асинхронного кода
- **Extension функции**: могут быть протестированы как обычные функции

### Структура тестов

**Kotlin** позволяет использовать более выразительные имена тестов благодаря поддержке обратных кавычек в именах функций. Это делает тесты более читаемыми и понятными.

## JUnit 5 с Kotlin

**JUnit** 5 отлично работает с **Kotlin** и предоставляет все необходимые возможности для тестирования.

### Базовые тесты

```kotlin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class CalculatorTest {
    @Test
    fun `should add two numbers`() {
        val calculator = Calculator()
        val result = calculator.add(2, 3)
        assertEquals(5, result)
    }

    @Test
    fun `should multiply two numbers`() {
        val calculator = Calculator()
        val result = calculator.multiply(4, 5)
        assertEquals(20, result)
    }
}
```

Использование обратных кавычек в именах тестов позволяет писать описания на естественном языке, что делает тесты самодокументируемыми. Это одна из особенностей **Kotlin**, которая улучшает читаемость тестов.

### Параметризованные тесты

**JUnit** 5 поддерживает параметризованные тесты, что особенно удобно в **Kotlin**:**

```kotlin
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

@ParameterizedTest
@CsvSource(
    "2, 3, 5",
    "10, 20, 30",
    "0, 0, 0"
)
fun `should add numbers correctly`(a: Int, b: Int, expected: Int) {
    val calculator = Calculator()
    assertEquals(expected, calculator.add(a, b))
}
```

Параметризованные тесты позволяют запускать один тест с разными наборами данных, что уменьшает дублирование кода и повышает покрытие тестами.

### Lifecycle методы

**JUnit** 5 предоставляет аннотации для методов жизненного цикла:**

```kotlin
import org.junit.jupiter.api.*

class LifecycleTest {
    @BeforeEach
    fun setUp() {
        // Выполняется перед каждым тестом
    }

    @AfterEach
    fun tearDown() {
        // Выполняется после каждого теста
    }

    @BeforeAll
    fun setUpAll() {
        // Выполняется один раз перед всеми тестами
    }

    @AfterAll
    fun tearDownAll() {
        // Выполняется один раз после всех тестов
    }
}
```

**Lifecycle** методы позволяют настроить окружение для тестов и выполнить **cleanup** после их завершения. Это особенно важно для тестов, которые работают с внешними ресурсами.

## MockK

**MockK** — это библиотека для мокирования в **Kotlin**, разработанная специально для этого языка.

### Базовое мокирование

```kotlin
import io.mockk.*

class UserServiceTest {
    @Test
    fun `should find user by id`() {
        val userRepository = mockk<UserRepository>()
        val user = User(id = 1, name = "Alice")

        every { userRepository.findById(1) } returns user

        val userService = UserService(userRepository)
        val result = userService.findById(1)

        assertEquals(user, result)
        verify { userRepository.findById(1) }
    }
}
```

**MockK** предоставляет более идиоматичный **API** для **Kotlin** по сравнению с **Mockito**. Функция `every` определяет поведение мока, а `verify` проверяет, что метод был вызван.

### Мокирование suspend функций

**MockK** поддерживает мокирование **suspend** функций:**

```kotlin
val repository = mockk<UserRepository>()

coEvery { repository.findByIdAsync(1) } returns user

val result = repository.findByIdAsync(1)
```

`coEvery` используется для мокирования **suspend** функций. Это критично для тестирования кода, использующего корутины.

### Relaxed моки

**Relaxed** моки возвращают значения по умолчанию для неопределенных вызовов:**

```kotlin
val repository = mockk<UserRepository>(relaxed = true)

// Не нужно определять поведение для всех методов
val result = repository.findById(1)  // Вернет null или значение по умолчанию
```

**Relaxed** моки полезны, когда нужно протестировать только определенные методы, а остальные не важны для текущего теста.

### Захват аргументов

**MockK** позволяет захватывать аргументы вызовов:**

```kotlin
val repository = mockk<UserRepository>()
val slot = slot<Long>()

every { repository.findById(capture(slot)) } returns user

repository.findById(1)

assertEquals(1, slot.captured)
```

Захват аргументов позволяет проверить, какие значения были переданы в методы мока, что полезно для проверки корректности вызовов.

## Kotlin Test Framework

**Kotlin Test Framework** предоставляет дополнительные возможности для тестирования **Kotlin** кода.

### Assertions

**Kotlin Test** предоставляет расширенные **assertions**:**

```kotlin
import io.kotest.assertions.*
import io.kotest.matchers.shouldBe

class UserTest {
    @Test
    fun `should have correct properties`() {
        val user = User(id = 1, name = "Alice")

        user.id shouldBe 1
        user.name shouldBe "Alice"
    }
}
```

**Infix** функции делают **assertions** более читаемыми. `shouldBe` является альтернативой `assertEquals` и читается как естественный язык.

### Matchers

**Kotlin Test** предоставляет богатый набор **matchers**:**

```kotlin
import io.kotest.matchers.*

list shouldHaveSize 3
string shouldContain "substring"
number shouldBeGreaterThan 0
```

**Matchers** делают проверки более выразительными и понятными. Они также предоставляют более информативные сообщения об ошибках.

## Тестирование корутин

Тестирование корутин требует специальных инструментов из-за их асинхронной природы.

### runTest

**Для тестирования корутин используется `runTest`:**

```kotlin
import kotlinx.coroutines.test.runTest

@Test
fun `should process data asynchronously`() = runTest {
    val service = DataService()
    val result = service.processData()
    assertEquals("Processed", result)
}
```

`runTest` создает тестовое окружение для корутин, которое позволяет контролировать время выполнения и избегать реальных задержек в тестах.

### Тестирование Flow

**Flow** можно тестировать через терминальные операции:**

```kotlin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest

@Test
fun `should emit values correctly`() = runTest {
    val flow = flowOf(1, 2, 3)
    val result = flow.toList()
    assertEquals(listOf(1, 2, 3), result)
}
```

Преобразование **Flow** в список позволяет легко проверить все испущенные значения. Это простой способ тестирования **Flow** без необходимости в сложных настройках.

### Тестирование suspend функций

**Suspend** функции тестируются так же, как обычные функции, но внутри `runTest`:**

```kotlin
@Test
fun `should fetch data asynchronously`() = runTest {
    val repository = mockk<DataRepository>()
    coEvery { repository.fetch() } returns "Data"

    val service = DataService(repository)
    val result = service.getData()

    assertEquals("Data", result)
}
```

Использование `coEvery` для мокирования **suspend** функций и `runTest` для выполнения теста обеспечивает корректное тестирование асинхронного кода.

## Property-Based Testing

**Property-Based Testing** позволяет тестировать свойства функций вместо конкретных примеров.

### KotlinTest Property Testing

```kotlin
import io.kotest.property.*

class MathTest {
    @Test
    fun `addition should be commutative`() {
        checkAll<Int, Int> { a, b ->
            (a + b) shouldBe (b + a)
        }
    }
}
```

**Property-based testing** генерирует случайные входные данные и проверяет, что свойство выполняется для всех случаев. Это помогает найти **edge cases**, которые могли быть пропущены при написании обычных тестов.

## Интеграционное тестирование

Интеграционные тесты проверяют взаимодействие между компонентами системы.

### Spring Boot тесты

**Для **Spring Boot** приложений используются специальные аннотации:**

```kotlin
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `should return user by id`() {
        mockMvc.get("/api/users/1")
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Alice"))
    }
}
```

**Spring Boot Test** предоставляет удобные **DSL** функции для тестирования **REST** контроллеров. `mockMvc` позволяет отправлять **HTTP** запросы и проверять ответы без запуска реального сервера.

### Тестирование с базой данных

**Для тестирования с реальной базой данных используется **Testcontainers**:**

```kotlin
@Testcontainers
class UserRepositoryTest {
    companion object {
        @Container
        val postgres = PostgreSQLContainer("postgres:13")
            .apply {
                withDatabaseName("testdb")
                withUsername("test")
                withPassword("test")
            }
    }

    @Test
    fun `should save and retrieve user`() {
        // Тест с реальной базой данных
    }
}
```

**Testcontainers** создает временные контейнеры с базами данных для тестов, что позволяет тестировать код с реальной БД без необходимости настройки окружения.

## Лучшие практики

### Именование тестов

**Используйте описательные имена тестов с обратными кавычками:**

```kotlin
// Хорошо
@Test
fun `should return user when id exists`() { }

// Плохо
@Test
fun testGetUser() { }
```

Описательные имена делают тесты самодокументируемыми и помогают понять, что именно тестируется, без чтения кода теста.

### Один assertion на тест

**Старайтесь проверять одну вещь в каждом тесте:**

```kotlin
// Хорошо
@Test
fun `should return correct name`() {
    val user = User(name = "Alice")
    assertEquals("Alice", user.name)
}

// Плохо
@Test
fun `should have all properties`() {
    val user = User(name = "Alice", age = 25)
    assertEquals("Alice", user.name)
    assertEquals(25, user.age)
    assertNotNull(user.id)
}
```

Один **assertion** на тест упрощает понимание, что именно не работает при падении теста, и делает тесты более фокусированными.

### Использование data классов

**Data** классы упрощают создание тестовых данных:**

```kotlin
val user = User(
    id = 1,
    name = "Alice",
    email = "alice@example.com"
)
```

Автоматическая генерация `equals` и `toString` в **data** классах упрощает сравнение объектов в тестах и отладку при падении тестов.

### Тестирование с использованием builders

**Используйте **builder** паттерн для создания тестовых данных:**

```kotlin
class UserBuilder {
    var id: Int = 1
    var name: String = "Test User"
    var email: String = "test@example.com"

    fun build() = User(id, name, email)
}

fun user(init: UserBuilder.() -> Unit = {}): User {
    return UserBuilder().apply(init).build()
}

// Использование
val user = user {
    name = "Alice"
    email = "alice@example.com"
}
```

**Builders** упрощают создание тестовых данных с различными комбинациями параметров, делая тесты более читаемыми и гибкими.

## Тестирование корутин

Тестирование асинхронного кода требует специальных подходов и инструментов.

### Использование runTest

**Kotlin** предоставляет `runTest` для тестирования корутин:**

```kotlin
import kotlinx.coroutines.test.runTest

@Test
fun `should fetch data asynchronously`() = runTest {
    val result = fetchData()
    assertEquals("data", result)
}

suspend fun fetchData(): String {
    delay(1000)
    return "data"
}
```

`runTest` автоматически управляет временем выполнения корутин в тестах, что позволяет тестировать асинхронный код синхронно и быстро.

### Тестирование Flow

**Для тестирования **Flow** используются специальные функции:**

```kotlin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest

@Test
fun `should emit values correctly`() = runTest {
    val flow = flowOf(1, 2, 3)

    flow.test {
        assertEquals(1, awaitItem())
        assertEquals(2, awaitItem())
        assertEquals(3, awaitItem())
        awaitComplete()
    }
}
```

Тестирование **Flow** позволяет проверять последовательность значений и завершение потока, что критично для реактивного кода.

### Мокирование suspend функций

**MockK** поддерживает мокирование **suspend** функций:**

```kotlin
class ApiServiceTest {
    @Test
    fun `should fetch user data`() = runTest {
        val mockApi = mockk<ApiService>()
        every { mockApi.fetchUser(1) } returns User(id = 1, name = "Alice")

        val result = mockApi.fetchUser(1)
        assertEquals("Alice", result.name)

        verify { mockApi.fetchUser(1) }
    }
}
```

Мокирование **suspend** функций работает так же, как и обычных функций, что упрощает тестирование асинхронного кода.

## Тестирование исключений

Проверка выброса исключений — важная часть тестирования.

### Использование assertThrows

**JUnit** 5 предоставляет `assertThrows` для проверки исключений:**

```kotlin
@Test
fun `should throw exception for invalid input`() {
    val exception = assertThrows<IllegalArgumentException> {
        validateInput(-1)
    }

    assertEquals("Input must be positive", exception.message)
}
```

`assertThrows` позволяет проверить тип исключения и его свойства, что делает тесты более точными.

### Использование shouldThrow в KotlinTest

**KotlinTest** предоставляет более идиоматичный способ проверки исключений:**

```kotlin
import io.kotest.assertions.throwables.shouldThrow

@Test
fun `should throw exception for invalid input`() {
    shouldThrow<IllegalArgumentException> {
        validateInput(-1)
    }.message shouldBe "Input must be positive"
}
```

**KotlinTest** синтаксис более читаемый и интегрирован с другими **assertion** функциями библиотеки.

## Параметризованные тесты

Параметризованные тесты позволяют запускать один тест с разными наборами данных.

### JUnit 5 параметризованные тесты

```kotlin
@ParameterizedTest
@ValueSource(ints = [2, 4, 6, 8])
fun `should be even number`(number: Int) {
    assertTrue(number % 2 == 0)
}

@ParameterizedTest
@CsvSource(
    "2, 3, 5",
    "10, 20, 30",
    "0, 0, 0"
)
fun `should add numbers correctly`(a: Int, b: Int, expected: Int) {
    assertEquals(expected, calculator.add(a, b))
}
```

Параметризованные тесты уменьшают дублирование кода и повышают покрытие тестами, позволяя легко добавлять новые тестовые случаи.

### KotlinTest табличное тестирование

**KotlinTest** предоставляет более выразительный способ параметризованного тестирования:**

```kotlin
@Test
fun `should calculate correctly`() {
    table(
        headers("a", "b", "expected"),
        row(2, 3, 5),
        row(10, 20, 30),
        row(0, 0, 0)
    ).forAll { a, b, expected ->
        calculator.add(a, b) shouldBe expected
    }
}
```

Табличное тестирование делает параметризованные тесты более читаемыми и позволяет легко добавлять новые строки с тестовыми данными.

## Тестирование приватных методов

Тестирование приватных методов требует специальных подходов.

### Использование @VisibleForTesting

**Помечайте методы как `@VisibleForTesting` для доступа из тестов:**

```kotlin
class Calculator {
    @VisibleForTesting
    internal fun validateInput(input: Int) {
        require(input > 0) { "Input must be positive" }
    }
}
```

`internal` видимость позволяет тестам в том же модуле обращаться к методам, не делая их публичными.

### Рефлексия для тестирования

**В крайних случаях можно использовать рефлексию:**

```kotlin
@Test
fun `should validate input`() {
    val calculator = Calculator()
    val method = calculator::class.java.getDeclaredMethod("validateInput", Int::class.java)
    method.isAccessible = true
    method.invoke(calculator, 10)
}
```

Однако использование рефлексии для тестирования приватных методов обычно указывает на проблему дизайна и должно быть последним средством.

## Тестирование с зависимостями

Управление зависимостями в тестах критично для изоляции и предсказуемости.

### Использование dependency injection

**Используйте `DI` для управления зависимостями в тестах:**

```kotlin
class UserService(
    private val userRepository: UserRepository,
    private val emailService: EmailService
) {
    fun createUser(name: String): User {
        val user = userRepository.save(User(name = name))
        emailService.sendWelcomeEmail(user)
        return user
    }
}

// В тестах
@Test
fun `should create user and send email`() {
    val mockRepository = mockk<UserRepository>()
    val mockEmailService = mockk<EmailService>()

    every { mockRepository.save(any()) } returns User(id = 1, name = "Alice")
    every { mockEmailService.sendWelcomeEmail(any()) } just Runs

    val service = UserService(mockRepository, mockEmailService)
    val user = service.createUser("Alice")

    assertEquals("Alice", user.name)
    verify { mockEmailService.sendWelcomeEmail(user) }
}
```

**Dependency injection** позволяет легко заменять зависимости на моки в тестах, что обеспечивает изоляцию и контроль над поведением зависимостей.

## Тестирование производительности

Иногда нужно тестировать не только корректность, но и производительность кода.

### Использование @Timeout

**JUnit** 5 позволяет устанавливать таймауты для тестов:**

```kotlin
@Test
@Timeout(value = 1, unit = TimeUnit.SECONDS)
fun `should complete quickly`() {
    // код должен завершиться за 1 секунду
}
```

Таймауты помогают выявить проблемы с производительностью на ранних этапах разработки.

### Бенчмаркинг в тестах

**Для более детального анализа производительности используйте бенчмаркинг:**

```kotlin
@Test
fun `should process large dataset efficiently`() {
    val data = generateLargeDataset()
    val startTime = System.nanoTime()

    val result = processData(data)

    val duration = System.nanoTime() - startTime
    assertTrue(duration < 1_000_000_000) // менее 1 секунды
}
```

Бенчмаркинг в тестах позволяет отслеживать деградацию производительности при изменениях кода.

## Организация тестов

Правильная организация тестов улучшает поддерживаемость и читаемость.

### Структура тестовых классов

**Организуйте тесты по функциональности:**

```kotlin
class UserServiceTest {
    // Группировка связанных тестов
    @Nested
    inner class `when creating user` {
        @Test
        fun `should create user with valid data`() { }

        @Test
        fun `should throw exception for invalid data`() { }
    }

    @Nested
    inner class `when updating user` {
        @Test
        fun `should update existing user`() { }

        @Test
        fun `should throw exception for non-existent user`() { }
    }
}
```

Использование `@Nested` классов позволяет логически группировать связанные тесты, что улучшает читаемость и организацию.

### Использование @BeforeEach и @AfterEach

**Настройка и очистка перед/после каждого теста:**

```kotlin
class DatabaseTest {
    private lateinit var database: Database

    @BeforeEach
    fun setUp() {
        database = Database.createInMemory()
    }

    @AfterEach
    fun tearDown() {
        database.close()
    }

    @Test
    fun `should save data`() {
        database.save(Data("test"))
        assertTrue(database.contains("test"))
    }
}
```

Правильная настройка и очистка обеспечивают изоляцию тестов и предотвращают влияние одного теста на другой.

## Покрытие кода

Измерение покрытия кода помогает понять, какие части кода не покрыты тестами.

### Настройка JaCoCo

**JaCoCo** — популярный инструмент для измерения покрытия кода:**

```kotlin
// build.gradle.kts
plugins {
    jacoco
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

**JaCoCo** генерирует отчеты о покрытии кода, которые помогают выявить непротестированные участки кода.

### Целевое покрытие

**Устанавливайте целевое покрытие для проекта:**

```kotlin
tasks.jacocoTestReport {
    executionData.setFrom(fileTree(layout.buildDirectory.dir("jacoco")).include("/*.exec"))

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("/generated/")
            }
        })
    )
}
```

Целевое покрытие помогает поддерживать качество кода и выявлять области, требующие дополнительного тестирования.

Этот файл содержит полное руководство по тестированию **Kotlin** кода, покрывающее все основные аспекты написания, организации и оптимизации тестов от базовых **unit** тестов до интеграционного тестирования и измерения покрытия кода.

## Тестирование с моками и стабами

### Использование MockK для продвинутых сценариев

**Продвинутое использование **MockK** для сложных сценариев тестирования:**

```kotlin
class AdvancedMocking {
    @Test
    fun testRelaxedMock() {
        val mock = mockk<Service>(relaxed = true)

        // Relaxed mock возвращает значения по умолчанию для всех методов
        val result = mock.process("test")
        assertNotNull(result)
    }

    @Test
    fun testSpy() {
        val realService = RealService()
        val spy = spyk(realService)

        // Spy позволяет переопределить некоторые методы
        every { spy.expensiveOperation() } returns "mocked"

        val result = spy.process()
        assertEquals("mocked", result)
    }

    @Test
    fun testObjectMock() {
        mockkObject(MyObject)

        every { MyObject.staticMethod() } returns "mocked"

        assertEquals("mocked", MyObject.staticMethod())

        unmockkObject(MyObject)
    }

    @Test
    fun testConstructorMock() {
        mockkConstructor(MyClass::class)

        every { anyConstructed<MyClass>().method() } returns "mocked"

        val instance = MyClass()
        assertEquals("mocked", instance.method())

        unmockkConstructor(MyClass::class)
    }
}
```

Продвинутое использование **MockK** позволяет тестировать сложные сценарии, включая статические методы и конструкторы.

### Тестирование с несколькими зависимостями

**Работа с несколькими зависимостями в тестах:**

```kotlin
class ComplexServiceTest {
    @Mock
    lateinit var repository: UserRepository

    @Mock
    lateinit var emailService: EmailService

    @Mock
    lateinit var logger: Logger

    @InjectMocks
    lateinit var service: UserService

    @Test
    fun testWithMultipleDependencies() {
        // Настройка всех моков
        every { repository.save(any()) } returns User(id = 1, name = "Alice")
        every { emailService.send(any()) } just Runs
        every { logger.info(any<String>()) } just Runs

        // Выполнение теста
        val user = service.createUser(User(name = "Alice"))

        // Проверка взаимодействий
        verify { repository.save(any()) }
        verify { emailService.send(any()) }
        verify { logger.info(any<String>()) }
    }
}
```

Работа с несколькими зависимостями требует правильной настройки всех моков и проверки взаимодействий.

## Performance тестирование

### Измерение производительности тестов

**Измерение производительности для выявления медленных тестов:**

```kotlin
@Test
@Timeout(value = 1, unit = TimeUnit.SECONDS)
fun testPerformance() {
    val startTime = System.nanoTime()

    // Выполнение операции
    val result = performOperation()

    val duration = System.nanoTime() - startTime
    assertTrue(duration < 100_000_000) // Менее 100ms

    assertNotNull(result)
}

// Настройка таймаутов для тестов
@Test
fun testWithTimeout() = runTest {
    withTimeout(500) {
        performLongRunningOperation()
    }
}
```

Измерение производительности тестов помогает выявлять медленные тесты и оптимизировать их.

### Нагрузочное тестирование

**Нагрузочное тестирование для проверки производительности под нагрузкой:**

```kotlin
@Test
fun testLoadPerformance() {
    val service = UserService(repository)
    val users = generateUsers(1000)

    val startTime = System.nanoTime()

    users.forEach { user ->
        service.createUser(user)
    }

    val duration = System.nanoTime() - startTime
    val averageTime = duration / users.size

    assertTrue(averageTime < 1_000_000) // Среднее время менее 1ms
}
```

Нагрузочное тестирование позволяет проверять производительность системы под нагрузкой и выявлять узкие места.

## Продвинутые техники тестирования

### Property-based testing

**Использование **property-based testing** для проверки свойств:**

```kotlin
import io.kotest.property.*
import io.kotest.property.arbitrary.*

// Property-based тест для коммутативности
class PropertyBasedTests : FunSpec({
    test("addition is commutative") {
        checkAll<Int, Int> { a, b ->
            (a + b) shouldBe (b + a)
        }
    }

    test("list reversal is idempotent") {
        checkAll<List<Int>> { list ->
            list.reversed().reversed() shouldBe list
        }
    }

    test("list concatenation is associative") {
        checkAll<List<Int>, List<Int>, List<Int>> { a, b, c ->
            ((a + b) + c) shouldBe (a + (b + c))
        }
    }
})

// Кастомные генераторы
val emailGenerator = Arb.string(
    pattern = "[a-z]+@[a-z]+\\.[a-z]+",
    minSize = 5,
    maxSize = 50
)

test("email validation") {
    checkAll(emailGenerator) { email ->
        email.contains("@") shouldBe true
        email.contains(".") shouldBe true
    }
}
```

**Property-based testing** позволяет проверять математические свойства функций, что обеспечивает более полное покрытие тестами.

### Mutation testing

**Использование **mutation testing** для оценки качества тестов:**

```kotlin
// Mutation testing концепция
// Мутации изменяют код, чтобы проверить, насколько хорошо тесты их обнаруживают

// Исходная функция
fun add(a: Int, b: Int): Int {
    return a + b
}

// Тест
@Test
fun `add should return sum`() {
    assertEquals(5, add(2, 3))
    assertEquals(0, add(-1, 1))
}

// Мутация 1: изменить оператор
fun add(a: Int, b: Int): Int {
    return a - b  // Мутация
}
// Тест должен упасть

// Мутация 2: изменить порядок
fun add(a: Int, b: Int): Int {
    return b + a  // Мутация (но результат тот же)
}
// Тест не упадет - недостаточное покрытие

// Улучшенный тест
@Test
fun `add should return sum with better coverage`() {
    // Проверка коммутативности
    assertEquals(add(2, 3), add(3, 2))

    // Проверка ассоциативности
    assertEquals(add(add(1, 2), 3), add(1, add(2, 3)))

    // Проверка нейтрального элемента
    assertEquals(5, add(5, 0))
    assertEquals(5, add(0, 5))
}
```

**Mutation testing** помогает выявлять слабые места в тестах и улучшать покрытие кода.

## Интеграционное тестирование

### Тестирование с базами данных

**Интеграционное тестирование с использованием баз данных:**

```kotlin
// Использование Testcontainers для тестирования с PostgreSQL
class DatabaseIntegrationTest : FunSpec({
    val postgres = PostgreSQLContainer("postgres:13")
        .withDatabaseName("test")
        .withUsername("test")
        .withPassword("test")

    beforeSpec {
        postgres.start()
    }

    afterSpec {
        postgres.stop()
    }

    test("should save and retrieve user") {
        val database = Database.connect(
            postgres.jdbcUrl,
            driver = "org.postgresql.Driver",
            user = postgres.username,
            password = postgres.password
        )

        transaction(database) {
            SchemaUtils.create(Users)

            Users.insert {
                it[name] = "Alice"
                it[email] = "alice@example.com"
            }

            val user = Users.selectAll().single()
            user[Users.name] shouldBe "Alice"
            user[Users.email] shouldBe "alice@example.com"
        }
    }
})

// Использование H2 для in-memory тестирования
class InMemoryDatabaseTest : FunSpec({
    val database = Database.connect(
        "jdbc:h2:mem:test",
        driver = "org.h2.Driver"
    )

    beforeTest {
        transaction(database) {
            SchemaUtils.create(Users)
        }
    }

    afterTest {
        transaction(database) {
            SchemaUtils.drop(Users)
        }
    }

    test("should work with in-memory database") {
        transaction(database) {
            Users.insert {
                it[name] = "Bob"
                it[email] = "bob@example.com"
            }

            val count = Users.selectAll().count()
            count shouldBe 1
        }
    }
})
```

Интеграционное тестирование с базами данных позволяет проверять правильность работы приложения с реальными системами хранения данных.

### Тестирование API

**Тестирование **REST API** с использованием **Ktor Test Client**:**

```kotlin
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*

class ApiTest : FunSpec({
    test("should return users") {
        testApplication {
            application {
                configureApplication()
            }

            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            val response: HttpResponse = client.get("/api/users")
            response.status shouldBe HttpStatusCode.OK

            val users: List<User> = response.body()
            users shouldNotBe empty()
        }
    }

    test("should create user") {
        testApplication {
            application {
                configureApplication()
            }

            val client = createClient {
                install(ContentNegotiation) {
                    json()
                }
            }

            val newUser = User(name = "Alice", email = "alice@example.com")
            val response: HttpResponse = client.post("/api/users") {
                contentType(ContentType.Application.Json)
                setBody(newUser)
            }

            response.status shouldBe HttpStatusCode.Created
            val created: User = response.body()
            created.name shouldBe "Alice"
            created.email shouldBe "alice@example.com"
        }
    }
})
```

Тестирование **API** позволяет проверять правильность работы веб-сервисов и взаимодействия между компонентами.

## Дополнительные техники тестирования

### Contract testing

**Использование **contract testing** для проверки контрактов между сервисами:**

```kotlin
// Contract тесты с использованием Pact
class UserServiceContractTest {
    @Test
    fun `should create user contract`() {
        val pact = ConsumerPactBuilder
            .consumer("user-service")
            .hasPactWith("user-api")
            .uponReceiving("a request to create a user")
            .path("/api/users")
            .method("POST")
            .body("""{"name": "Alice", "email": "alice@example.com"}""")
            .willRespondWith()
            .status(201)
            .body("""{"id": 1, "name": "Alice", "email": "alice@example.com"}""")
            .toPact()

        pact.verify { mockServer ->
            val response = HttpClient().post("${mockServer.url}/api/users") {
                contentType(ContentType.Application.Json)
                setBody("""{"name": "Alice", "email": "alice@example.com"}""")
            }

            assertEquals(201, response.status.value)
        }
    }
}
```

**Contract testing** позволяет проверять совместимость между сервисами и предотвращать **breaking changes**.

### Snapshot testing

**Использование **snapshot testing** для проверки вывода:**

```kotlin
// Snapshot тесты
class SnapshotTest {
    @Test
    fun `should match snapshot`() {
        val data = generateReport()
        val snapshot = data.toString()

        val expectedSnapshot = File("src/test/resources/snapshots/report.snapshot")
            .readText()

        if (snapshot != expectedSnapshot) {
            // Сохранить новый snapshot для проверки
            File("src/test/resources/snapshots/report.snapshot.new")
                .writeText(snapshot)
            fail("Snapshot mismatch. Check report.snapshot.new")
        }
    }
}
```

**Snapshot testing** позволяет проверять сложные структуры данных и предотвращать регрессии.

Этот файл содержит полное руководство по тестированию **Kotlin** кода, покрывающее все основные аспекты написания, организации и оптимизации тестов от базовых **unit** тестов до интеграционного тестирования, измерения покрытия кода, тестирования с моками и стабами, **performance** тестирования, **property-based testing**, **mutation testing**, интеграционного тестирования с базами данных, тестирования **API**, **contract testing** и **snapshot testing**.

## Дополнительные техники тестирования

### Golden File Testing

**Использование **golden file testing** для проверки вывода:**

```kotlin
// Golden file тесты
class GoldenFileTest {
    @Test
    fun `should match golden file`() {
        val output = generateReport()
        val goldenFile = File("src/test/resources/golden/report.txt")

        if (!goldenFile.exists()) {
            goldenFile.writeText(output)
            return
        }

        val expected = goldenFile.readText()
        assertEquals(expected, output, "Output doesn't match golden file")
    }
}
```

**Golden file testing** позволяет проверять сложные структурированные выводы и предотвращать регрессии.

### Visual Regression Testing

**Использование **visual regression testing**:**

```kotlin
// Visual regression тесты
class VisualRegressionTest {
    @Test
    fun `should match visual snapshot`() {
        val screenshot = captureScreenshot()
        val expected = File("src/test/resources/snapshots/ui.png")

        if (!expected.exists()) {
            screenshot.save(expected)
            return
        }

        val diff = compareImages(screenshot, expected)
        assertTrue(diff < 0.01, "Visual difference too large")
    }
}
```

**Visual regression testing** позволяет проверять визуальные изменения в `UI`.

Этот файл содержит полное руководство по тестированию **Kotlin** кода, покрывающее все основные аспекты написания, организации и оптимизации тестов от базовых **unit** тестов до интеграционного тестирования, измерения покрытия кода, тестирования с моками и стабами, **performance** тестирования, **property-based testing**, **mutation testing**, интеграционного тестирования с базами данных, тестирования **API**, **contract testing**, **snapshot testing**, **golden file testing** и **visual regression testing**.

## Дополнительные ресурсы

**Для дальнейшего изучения тестирования в **Kotlin** рекомендуется:**

- **JUnit** 5 **Documentation**: **https**://**junit.org**/**junit5**/**docs**/**current**/**user-guide**/
- **MockK Documentation**: **https**://**mockk.io**/
- **Kotlin Test Framework**: **https**://**github.com**/**kotlintest**/**kotlintest**
- **Property-Based Testing**: **https**://**github.com**/**kotlintest**/**kotlintest**

Этот файл содержит полное руководство по тестированию **Kotlin** кода, покрывающее все основные аспекты написания, организации и оптимизации тестов от базовых **unit** тестов до интеграционного тестирования, измерения покрытия кода, тестирования с моками и стабами, **performance** тестирования, **property-based testing**, **mutation testing**, интеграционного тестирования с базами данных, тестирования **API**, **contract testing**, **snapshot testing**, **golden file testing**, **visual regression testing**, заключение и дополнительные ресурсы.

## Итоговые рекомендации

**При тестировании **Kotlin** кода рекомендуется:**

1. Писать **unit** тесты для всех публичных методов
2. Использовать моки и стабы для изоляции зависимостей
3. Применять **property-based testing** для проверки свойств
4. Измерять покрытие кода для выявления непротестированных участков
5. Использовать интеграционные тесты для проверки взаимодействия компонентов

Этот файл содержит полное руководство по тестированию **Kotlin** кода, покрывающее все основные аспекты написания, организации и оптимизации тестов от базовых **unit** тестов до интеграционного тестирования, измерения покрытия кода, тестирования с моками и стабами, **performance** тестирования, **property-based testing**, **mutation testing**, интеграционного тестирования с базами данных, тестирования **API**, **contract testing**, **snapshot testing**, **golden file testing**, **visual regression testing**, заключение, дополнительные ресурсы и итоговые рекомендации.

## Практические примеры использования

### Тестирование сервиса с зависимостями

**Пример тестирования сервиса с использованием моков:**

```kotlin
class UserServiceTest {
    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should return user when found`() {
        val userId = 1L
        val user = User(userId, "John", "john@example.com")

        every { userRepository.findById(userId) } returns user

        val result = userService.getUser(userId)

        assertEquals(user, result)
        verify { userRepository.findById(userId) }
    }
}
```

Использование моков позволяет изолировать тестируемый код от зависимостей.

### Интеграционное тестирование API

**Пример интеграционного тестирования **REST API**:**

```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserApiIntegrationTest {
    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Test
    fun `should create and retrieve user`() {
        val user = User(null, "John", "john@example.com")

        val created = restTemplate.postForEntity(
            "/api/users",
            user,
            User::class.java
        )

        assertEquals(HttpStatus.CREATED, created.statusCode)

        val retrieved = restTemplate.getForEntity(
            "/api/users/${created.body?.id}",
            User::class.java
        )

        assertEquals(HttpStatus.OK, retrieved.statusCode)
        assertEquals("John", retrieved.body?.name)
    }
}
```

Интеграционные тесты проверяют взаимодействие компонентов системы.

### Тестирование корутин

**Пример тестирования асинхронного кода с корутинами:**

```kotlin
import kotlinx.coroutines.test.runTest

class CoroutineTest {
    @Test
    fun `should process data asynchronously`() = runTest {
        val repository = TestUserRepository()
        val service = UserService(repository)

        val user = service.createUser(User(0, "John", "john@example.com"))

        assertEquals("John", user.name)
        assertEquals(1, repository.users.size)
    }

    @Test
    fun `should handle errors in coroutines`() = runTest {
        val repository = FailingUserRepository()
        val service = UserService(repository)

        assertThrows<Exception> {
            service.createUser(User(0, "John", "john@example.com"))
        }
    }
}
```

Использование **runTest** позволяет тестировать корутины синхронно и предсказуемо.

### Тестирование Flow

**Пример тестирования **Kotlin Flow**:**

```kotlin
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class FlowTest {
    @Test
    fun `should emit values in flow`() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5)
        val result = flow.toList()

        assertEquals(listOf(1, 2, 3, 4, 5), result)
    }

    @Test
    fun `should transform flow values`() = runTest {
        val flow = flowOf(1, 2, 3)
            .map { it * 2 }

        val result = flow.toList()
        assertEquals(listOf(2, 4, 6), result)
    }
}
```

Тестирование **Flow** позволяет проверять реактивные потоки данных.

### Практические примеры: Тестирование с Testcontainers

```kotlin
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class DatabaseIntegrationTest {
    @Container
    val postgres = PostgreSQLContainer("postgres:13")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")

    @Test
    fun `should connect to test database`() {
        val url = postgres.jdbcUrl
        val connection = DriverManager.getConnection(url, "test", "test")
        assertTrue(connection.isValid(5))
    }
}
```

### Практические примеры: Тестирование с WireMock

```kotlin
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*

class ApiClientTest {
    private val wireMockServer = WireMockServer(8089)

    @BeforeEach
    fun setup() {
        wireMockServer.start()
    }

    @AfterEach
    fun tearDown() {
        wireMockServer.stop()
    }

    @Test
    fun `should fetch user from API`() {
        wireMockServer.stubFor(
            get(urlEqualTo("/users/1"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"id":1,"name":"Alice"}""")
                )
        )

        val client = ApiClient("http://localhost:8089")
        val user = client.getUser(1)

        assertEquals("Alice", user.name)
    }
}
```

### Практические примеры: Property-based тестирование

```kotlin
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll

class MathPropertiesTest {
    @Test
    fun `addition is commutative`() = checkAll(
        Arb.int(),
        Arb.int()
    ) { a, b ->
        assertEquals(a + b, b + a)
    }

    @Test
    fun `reverse twice returns original`() = checkAll(Arb.list(Arb.int())) { list ->
        assertEquals(list, list.reversed().reversed())
    }

    @Test
    fun `list size is preserved after map`() = checkAll(
        Arb.list(Arb.int())
    ) { list ->
        assertEquals(list.size, list.map { it * 2 }.size)
    }
}
```

Этот файл содержит полное руководство по тестированию **Kotlin** кода, покрывающее все основные аспекты написания, организации и оптимизации тестов от базовых **unit** тестов до интеграционного тестирования, измерения покрытия кода, тестирования с моками и стабами, **performance** тестирования, **property-based testing**, **mutation testing**, интеграционного тестирования с базами данных, тестирования **API**, **contract testing**, **snapshot testing**, **golden file testing**, **visual regression testing**, тестирования с **Testcontainers**, **WireMock**, **property-based** тестирования, практические примеры использования, включая тестирование корутин и **Flow**, заключение, дополнительные ресурсы и итоговые рекомендации.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Тестирование является критически важной частью разработки качественного программного обеспечения. Понимание различных техник тестирования, от базовых **unit** тестов до продвинутых подходов, таких как **property-based testing**, **mutation testing**, **contract testing**, **visual regression testing**, тестирования с **Testcontainers** и **WireMock**, позволяет создавать надежные, проверенные и поддерживаемые приложения. Правильная организация тестов, измерение покрытия кода, использование различных инструментов тестирования и автоматизация тестирования помогают выявлять ошибки на ранних этапах разработки и обеспечивают стабильность приложений.

Этот файл содержит полное руководство по тестированию **Kotlin** кода, покрывающее все основные аспекты написания, организации и оптимизации тестов от базовых **unit** тестов до интеграционного тестирования, измерения покрытия кода, тестирования с моками и стабами, **performance** тестирования, **property-based testing**, **mutation testing**, интеграционного тестирования с базами данных, тестирования **API**, **contract testing**, **snapshot testing**, **golden file testing**, **visual regression testing**, тестирования с **Testcontainers** и **WireMock** и заключение.

