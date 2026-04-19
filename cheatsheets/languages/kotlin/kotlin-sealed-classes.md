---
title: "Sealed классы и интерфейсы в Kotlin"
description: "Краткое руководство по sealed классам и sealed интерфейсам в Kotlin - ограниченные иерархии типов для типобезопасного кода."
tags:
  - languages
  - kotlin
  - kotlin-sealed-classes
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Sealed классы и интерфейсы в Kotlin

Краткое руководство по **sealed** классам и **sealed** интерфейсам в **Kotlin** - ограниченные иерархии типов для типобезопасного кода.

**Последнее обновление**: 2024-01-`XX`

## Полезные ссылки

[Kotlin Documentation](https://kotlinlang.org/docs/home.html)
[Kotlin GitHub](https://github.com/JetBrains/kotlin)

## Содержание

- [Введение](#введение)
- [**Sealed** классы](#sealed-классы)
  - [Базовое определение](#базовое-определение)
  - [**Sealed** классы для состояний](#sealed-классы-для-состояний)
  - [**Sealed** классы для выражения действий](#sealed-классы-для-выражения-действий)
  - [**Sealed** классы для парсинга](#sealed-классы-для-парсинга)
- [**Sealed** интерфейсы](#sealed-интерфейсы)
  - [Базовое использование](#базовое-использование)
  - [**Sealed** интерфейсы для различных доменов](#sealed-интерфейсы-для-различных-доменов)
- [**When** выражения и **exhaustiveness**](#when-выражения-и-exhaustiveness)
  - [**Exhaustive when**](#exhaustive-when)
  - [**Non-exhaustive when**](#non-exhaustive-when)
  - [**When** как выражение](#when-как-выражение)
- [Паттерны использования](#паттерны-использования)
  - [**State Machine**](#state-machine)
  - [**Command Pattern**](#command-pattern)
  - [**Visitor Pattern**](#visitor-pattern)
  - [**Event System**](#event-system)
  - [**Algebraic Data Types**](#algebraic-data-types)
- [Расширения для **sealed** классов](#расширения-для-sealed-классов)
  - [**Extension** функции](#extension-функции)
  - [**Utility** функции](#utility-функции)
- [**Best practices**](#best-practices)
  - [1. Используйте **sealed** классы для ограниченных наборов типов](#1-используйте-sealed-классы-для-ограниченных-наборов-типов)
  - [2. Используйте **object** для **singleton** случаев](#2-используйте-object-для-singleton-случаев)
  - [3. Предпочитайте **sealed** классы **enum**'ам для сложных данных](#3-предпочитайте-sealed-классы-enumам-для-сложных-данных)
  - [4. Используйте **generic** параметры для типобезопасности](#4-используйте-generic-параметры-для-типобезопасности)
  - [Практические примеры: **AST** (**Abstract Syntax Tree**)](#практические-примеры-ast-abstract-syntax-tree)
  - [Практические примеры: **HTTP** запросы и ответы](#практические-примеры-http-запросы-и-ответы)
  - [Практические примеры: Валидация форм](#практические-примеры-валидация-форм)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Sealed** классы и интерфейсы в **Kotlin** представляют ограниченные иерархии типов, где все возможные подтипы известны на этапе компиляции. Это обеспечивает типобезопасность и позволяет компилятору проверять полноту **when**-выражений.

**Sealed** классы идеально подходят для моделирования состояний, результатов операций, вариантов выбора и других случаев, когда набор возможных типов ограничен.

## **Sealed** классы

### Базовое определение

```kotlin
sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Error(val message: String, val cause: Throwable? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Использование
fun handleResult(result: Result<String>) {
    when (result) {
        is Result.Success -> println("Success: ${result.value}")
        is Result.Error -> println("Error: ${result.message}")
        is Result.Loading -> println("Loading...")
        // Компилятор требует обработки всех случаев
    }
}
```

### **Sealed** классы для состояний

```kotlin
// Ограниченный набор состояний сети (idle, loading, success, error)
sealed class NetworkState {
    object Idle : NetworkState()
    object Loading : NetworkState()
    data class Success(val data: String) : NetworkState()
    data class Error(val message: String, val code: Int) : NetworkState()
}

class NetworkManager {
    private var state: NetworkState = NetworkState.Idle
    
    fun setState(newState: NetworkState) {
        state = newState
        onStateChanged(state)
    }
    
    private fun onStateChanged(state: NetworkState) {
        when (state) {
            is NetworkState.Idle -> println("Network idle")
            is NetworkState.Loading -> println("Loading data...")
            is NetworkState.Success -> println("Data loaded: ${state.data}")
            is NetworkState.Error -> println("Error ${state.code}: ${state.message}")
        }
    }
}
```

### **Sealed** классы для выражения действий

```kotlin
sealed class Action {
    object NavigateBack : Action()
    data class NavigateTo(val route: String, val params: Map<String, String> = emptyMap()) : Action()
    data class ShowDialog(val title: String, val message: String) : Action()
    data class ShowSnackbar(val message: String, val duration: Long = 3000) : Action()
}

class ActionHandler {
    fun handleAction(action: Action) {
        when (action) {
            is Action.NavigateBack -> navigateBack()
            is Action.NavigateTo -> navigate(action.route, action.params)
            is Action.ShowDialog -> showDialog(action.title, action.message)
            is Action.ShowSnackbar -> showSnackbar(action.message, action.duration)
        }
    }
    
    private fun navigateBack() { /* ... */ }
    private fun navigate(route: String, params: Map<String, String>) { /* ... */ }
    private fun showDialog(title: String, message: String) { /* ... */ }
    private fun showSnackbar(message: String, duration: Long) { /* ... */ }
}
```

### **Sealed** классы для парсинга

```kotlin
sealed class ParseResult<out T> {
    data class Success<T>(val value: T, val remaining: String) : ParseResult<T>()
    data class Failure(val message: String, val position: Int) : ParseResult<Nothing>()
}

class Parser {
    fun parseNumber(input: String): ParseResult<Int> {
        val trimmed = input.trim()
        val number = trimmed.toIntOrNull()
        
        return when {
            number != null -> ParseResult.Success(number, "")
            else -> ParseResult.Failure("Invalid number", 0)
        }
    }
    
    fun parseExpression(input: String): ParseResult<Expression> {
        // Реализация парсинга
        return ParseResult.Failure("Not implemented", 0)
    }
}

sealed class Expression {
    data class Number(val value: Int) : Expression()
    data class Add(val left: Expression, val right: Expression) : Expression()
    data class Multiply(val left: Expression, val right: Expression) : Expression()
}
```

## **Sealed** интерфейсы

**Sealed** интерфейсы позволяют создавать ограниченные иерархии для интерфейсов, особенно полезны для множественного наследования.

### Базовое использование

```kotlin
sealed interface Response
sealed interface Request

sealed class ApiCall : Response, Request {
    data class Success(val data: String) : ApiCall()
    data class Error(val message: String) : ApiCall()
    object Timeout : ApiCall()
}

// Можно использовать каждый интерфейс отдельно
fun handleResponse(response: Response) {
    when (response) {
        is ApiCall.Success -> println("Success: ${response.data}")
        is ApiCall.Error -> println("Error: ${response.message}")
        is ApiCall.Timeout -> println("Timeout")
    }
}
```

### **Sealed** интерфейсы для различных доменов

```kotlin
sealed interface PaymentMethod {
    val id: String
}

sealed interface Authenticated {
    val token: String
}

data class CreditCard(
    override val id: String,
    val number: String,
    val cvv: String
) : PaymentMethod, Authenticated {
    override val token: String = generateToken()
}

data class PayPal(
    override val id: String,
    val email: String
) : PaymentMethod, Authenticated {
    override val token: String = generateToken()
}

object Cash : PaymentMethod {
    override val id: String = "cash"
}

fun processPayment(method: PaymentMethod) {
    when (method) {
        is CreditCard -> processCardPayment(method)
        is PayPal -> processPayPalPayment(method)
        is Cash -> processCashPayment()
    }
}

fun authenticate(method: Authenticated) {
    validateToken(method.token)
}
```

## **When** выражения и **exhaustiveness**

Одна из главных особенностей **sealed** классов - проверка полноты **when**-выражений.

### **Exhaustive when**

```kotlin
sealed class Direction {
    object North : Direction()
    object South : Direction()
    object East : Direction()
    object West : Direction()
}

fun getDirectionName(direction: Direction): String {
    return when (direction) {
        is Direction.North -> "North"
        is Direction.South -> "South"
        is Direction.East -> "East"
        is Direction.West -> "West"
        // Компилятор гарантирует, что все случаи обработаны
    }
}

// Without else - компилятор проверяет полноту
fun move(direction: Direction): Unit = when (direction) {
    Direction.North -> moveNorth()
    Direction.South -> moveSouth()
    Direction.East -> moveEast()
    Direction.West -> moveWest()
}
```

### **Non-exhaustive when**

```kotlin
fun handlePartial(direction: Direction) {
    when (direction) {
        Direction.North -> println("North")
        Direction.South -> println("South")
        // Компилятор предупредит о неполном when
        else -> println("Other direction")
    }
}
```

### **When** как выражение

```kotlin
sealed class Status {
    object Active : Status()
    object Inactive : Status()
    object Suspended : Status()
}

fun getStatusPriority(status: Status): Int = when (status) {
    Status.Active -> 1
    Status.Inactive -> 2
    Status.Suspended -> 3
    // Возвращает значение напрямую
}

val priority = getStatusPriority(Status.Active) // 1
```

## Паттерны использования

### **State Machine**

```kotlin
sealed class State {
    object Initial : State()
    data class Loading(val progress: Int = 0) : State()
    data class Loaded<T>(val data: T) : State()
    data class Error(val exception: Throwable) : State()
}

class StateMachine {
    private var state: State = State.Initial
    
    fun transition(newState: State) {
        val oldState = state
        state = newState
        
        when (state) {
            is State.Initial -> onInitial()
            is State.Loading -> onLoading(state.progress)
            is State.Loaded<*> -> onLoaded(state.data)
            is State.Error -> onError(state.exception)
        }
    }
    
    private fun onInitial() { /* ... */ }
    private fun onLoading(progress: Int) { /* ... */ }
    private fun onLoaded(data: Any) { /* ... */ }
    private fun onError(exception: Throwable) { /* ... */ }
}
```

### **Command Pattern**

```kotlin
sealed class Command {
    object Undo : Command()
    object Redo : Command()
    data class Add(val item: String) : Command()
    data class Remove(val item: String) : Command()
    data class Update(val oldItem: String, val newItem: String) : Command()
}

class CommandProcessor {
    private val history = mutableListOf<String>()
    private var position = -1
    
    fun execute(command: Command) {
        when (command) {
            is Command.Undo -> undo()
            is Command.Redo -> redo()
            is Command.Add -> add(command.item)
            is Command.Remove -> remove(command.item)
            is Command.Update -> update(command.oldItem, command.newItem)
        }
    }
    
    private fun undo() {
        if (position > 0) position--
    }
    
    private fun redo() {
        if (position < history.size - 1) position++
    }
    
    private fun add(item: String) {
        history.add(item)
        position = history.size - 1
    }
    
    private fun remove(item: String) {
        history.remove(item)
    }
    
    private fun update(oldItem: String, newItem: String) {
        val index = history.indexOf(oldItem)
        if (index >= 0) {
            history[index] = newItem
        }
    }
}
```

### **Visitor Pattern**

```kotlin
sealed class Node {
    data class File(val name: String, val size: Long) : Node()
    data class Directory(val name: String, val children: List<Node>) : Node()
}

interface NodeVisitor<T> {
    fun visitFile(file: Node.File): T
    fun visitDirectory(directory: Node.Directory): T
}

class SizeVisitor : NodeVisitor<Long> {
    override fun visitFile(file: Node.File): Long = file.size
    
    override fun visitDirectory(directory: Node.Directory): Long {
        return directory.children.sumOf { node ->
            when (node) {
                is Node.File -> visitFile(node)
                is Node.Directory -> visitDirectory(node)
            }
        }
    }
}

fun Node.accept(visitor: NodeVisitor<Long>): Long = when (this) {
    is Node.File -> visitor.visitFile(this)
    is Node.Directory -> visitor.visitDirectory(this)
}
```

### **Event System**

```kotlin
sealed class Event {
    data class UserLogin(val userId: String, val timestamp: Long) : Event()
    data class UserLogout(val userId: String, val timestamp: Long) : Event()
    data class Purchase(val userId: String, val productId: String, val amount: Double) : Event()
    data class PageView(val userId: String, val page: String) : Event()
}

class EventProcessor {
    fun processEvent(event: Event) {
        when (event) {
            is Event.UserLogin -> handleLogin(event.userId, event.timestamp)
            is Event.UserLogout -> handleLogout(event.userId, event.timestamp)
            is Event.Purchase -> handlePurchase(event.userId, event.productId, event.amount)
            is Event.PageView -> handlePageView(event.userId, event.page)
        }
    }
    
    private fun handleLogin(userId: String, timestamp: Long) { /* ... */ }
    private fun handleLogout(userId: String, timestamp: Long) { /* ... */ }
    private fun handlePurchase(userId: String, productId: String, amount: Double) { /* ... */ }
    private fun handlePageView(userId: String, page: String) { /* ... */ }
}
```

### **Algebraic Data Types**

```kotlin
sealed class Either<out L, out R> {
    data class Left<L>(val value: L) : Either<L, Nothing>()
    data class Right<R>(val value: R) : Either<Nothing, R>()
    
    fun <T> fold(
        left: (L) -> T,
        right: (R) -> T
    ): T = when (this) {
        is Left -> left(value)
        is Right -> right(value)
    }
    
    fun <T> map(f: (R) -> T): Either<L, T> = when (this) {
        is Left -> this
        is Right -> Right(f(value))
    }
    
    fun <T> flatMap(f: (R) -> Either<L, T>): Either<L, T> = when (this) {
        is Left -> this
        is Right -> f(value)
    }
}

// Использование
fun divide(a: Int, b: Int): Either<String, Int> {
    return if (b == 0) {
        Either.Left("Division by zero")
    } else {
        Either.Right(a / b)
    }
}

val result = divide(10, 2)
result.fold(
    left = { error -> println("Error: $error") },
    right = { value -> println("Result: $value") }
)
```

## Расширения для **sealed** классов

### **Extension** функции

```kotlin
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<String>) : ValidationResult()
}

fun ValidationResult.isValid(): Boolean = this is ValidationResult.Valid

fun ValidationResult.errorsOrEmpty(): List<String> = when (this) {
    is ValidationResult.Valid -> emptyList()
    is ValidationResult.Invalid -> errors
}

fun ValidationResult.getOrThrow(): Unit = when (this) {
    is ValidationResult.Valid -> Unit
    is ValidationResult.Invalid -> throw ValidationException(errors)
}

class ValidationException(val errors: List<String>) : Exception(errors.joinToString())
```

### **Utility** функции

```kotlin
sealed class Result<out T, out E> {
    data class Success<T>(val value: T) : Result<T, Nothing>()
    data class Failure<E>(val error: E) : Result<Nothing, E>()
    
    fun <R> map(f: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success(f(value))
        is Failure -> this
    }
    
    fun <R> flatMap(f: (T) -> Result<R, E>): Result<R, E> = when (this) {
        is Success -> f(value)
        is Failure -> this
    }
    
    fun getOrElse(default: () -> T): T = when (this) {
        is Success -> value
        is Failure -> default()
    }
    
    fun getOrNull(): T? = when (this) {
        is Success -> value
        is Failure -> null
    }
}
```

## **Best practices**

### 1. Используйте **sealed** классы для ограниченных наборов типов

```kotlin
// ✅ Хорошо - ограниченный набор состояний
sealed class OrderStatus {
    object Pending : OrderStatus()
    object Processing : OrderStatus()
    object Shipped : OrderStatus()
    object Delivered : OrderStatus()
    data class Cancelled(val reason: String) : OrderStatus()
}

// ❌ Плохо - неограниченный набор типов
open class Status
class Pending : Status()
class Processing : Status()
// Можно добавить бесконечно много подклассов
```

### 2. Используйте **object** для **singleton** случаев

```kotlin
sealed class Response {
    // ✅ Хорошо - object для singleton
    object Loading : Response()
    object Empty : Response()
    
    // ✅ Хорошо - data class для случаев с данными
    data class Success(val data: String) : Response()
    data class Error(val message: String) : Response()
}
```

### 3. Предпочитайте **sealed** классы **enum**'ам для сложных данных

```kotlin
// ✅ Sealed классы для случаев с данными
sealed class Status {
    data class Loading(val progress: Int) : Status()
    data class Error(val exception: Throwable) : Status()
    object Success : Status()
}

// Enum только для простых констант
enum class Priority {
    LOW, MEDIUM, HIGH
}
```

### 4. Используйте **generic** параметры для типобезопасности

```kotlin
sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure(val error: Throwable) : Result<Nothing>()
    
    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }
}
```

### Практические примеры: **AST** (`Abstract Syntax Tree`)

```kotlin
sealed class Expr {
    data class Number(val value: Int) : Expr()
    data class Variable(val name: String) : Expr()
    data class Add(val left: Expr, val right: Expr) : Expr()
    data class Multiply(val left: Expr, val right: Expr) : Expr()
    data class FunctionCall(val name: String, val args: List<Expr>) : Expr()
}

class ExpressionEvaluator(private val variables: Map<String, Int>) {
    fun evaluate(expr: Expr): Int = when (expr) {
        is Expr.Number -> expr.value
        is Expr.Variable -> variables[expr.name] ?: throw IllegalArgumentException("Variable ${expr.name} not found")
        is Expr.Add -> evaluate(expr.left) + evaluate(expr.right)
        is Expr.Multiply -> evaluate(expr.left) * evaluate(expr.right)
        is Expr.FunctionCall -> evaluateFunction(expr.name, expr.args)
    }
    
    private fun evaluateFunction(name: String, args: List<Expr>): Int {
        val evaluatedArgs = args.map { evaluate(it) }
        return when (name) {
            "max" -> evaluatedArgs.maxOrNull() ?: 0
            "min" -> evaluatedArgs.minOrNull() ?: 0
            "sum" -> evaluatedArgs.sum()
            else -> throw IllegalArgumentException("Unknown function: $name")
        }
    }
}
```

### Практические примеры: **HTTP** запросы и ответы

```kotlin
sealed class HttpRequest {
    data class Get(val path: String, val headers: Map<String, String> = emptyMap()) : HttpRequest()
    data class Post(val path: String, val body: String, val headers: Map<String, String> = emptyMap()) : HttpRequest()
    data class Put(val path: String, val body: String, val headers: Map<String, String> = emptyMap()) : HttpRequest()
    data class Delete(val path: String, val headers: Map<String, String> = emptyMap()) : HttpRequest()
}

sealed class HttpResponse {
    data class Success(val statusCode: Int, val body: String, val headers: Map<String, String>) : HttpResponse()
    data class Error(val statusCode: Int, val message: String, val headers: Map<String, String>) : HttpResponse()
    data class Redirect(val statusCode: Int, val location: String) : HttpResponse()
}

class HttpClient {
    fun execute(request: HttpRequest): HttpResponse {
        return when (request) {
            is HttpRequest.Get -> handleGet(request)
            is HttpRequest.Post -> handlePost(request)
            is HttpRequest.Put -> handlePut(request)
            is HttpRequest.Delete -> handleDelete(request)
        }
    }
    
    private fun handleGet(request: HttpRequest.Get): HttpResponse { /* ... */ }
    private fun handlePost(request: HttpRequest.Post): HttpResponse { /* ... */ }
    private fun handlePut(request: HttpRequest.Put): HttpResponse { /* ... */ }
    private fun handleDelete(request: HttpRequest.Delete): HttpResponse { /* ... */ }
}
```

### Практические примеры: Валидация форм

```kotlin
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val errors: List<ValidationError>) : ValidationResult()
}

sealed class ValidationError {
    data class Required(val field: String) : ValidationError()
    data class MinLength(val field: String, val min: Int) : ValidationError()
    data class MaxLength(val field: String, val max: Int) : ValidationError()
    data class Pattern(val field: String, val message: String) : ValidationError()
    data class Custom(val field: String, val message: String) : ValidationError()
}

class FormValidator {
    fun validateForm(form: Map<String, String>): ValidationResult {
        val errors = mutableListOf<ValidationError>()
        
        if (form["name"].isNullOrBlank()) {
            errors.add(ValidationError.Required("name"))
        }
        
        val email = form["email"] ?: ""
        if (email.isBlank()) {
            errors.add(ValidationError.Required("email"))
        } else if (!email.contains("@")) {
            errors.add(ValidationError.Pattern("email", "Invalid email format"))
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}
```


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Sealed** классы и интерфейсы - это инструмент **Kotlin** для создания типобезопасных ограниченных иерархий типов. Они обеспечивают **compile-time** проверку полноты **when**-выражений, что помогает избежать ошибок и делает код более надежным.

Использование **sealed** классов для состояний, результатов операций, команд, событий, **AST**, **HTTP** запросов, валидации и других паттернов позволяет создавать выразительный, безопасный и легко расширяемый код.

## Дополнительные ресурсы

- [Kotlin Sealed Classes Documentation](https://kotlinlang.org/docs/sealed-classes.html)
- [Kotlin Sealed Interfaces](https://kotlinlang.org/docs/sealed-classes.html)

