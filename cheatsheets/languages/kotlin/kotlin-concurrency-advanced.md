---
title: "Kotlin Concurrency: Advanced"
description: "Кратко: продвинутое руководство по конкурентности в Kotlin: Flow, Channel, Mutex, Structured Concurrency, Coroutine Context, Job, Deferred и другие продвинутые темы."
tags:
  - languages
  - kotlin
  - kotlin-concurrency-advanced
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Kotlin Concurrency: Advanced

Кратко: продвинутое руководство по конкурентности в **Kotlin**: **Flow**, **Channel**, **Mutex**, **Structured Concurrency**, **Coroutine Context**, **Job**, **Deferred** и другие продвинутые темы.

## Полезные ссылки

### Официальная документация
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html)
- [Kotlin Channels](https://kotlinlang.org/docs/channels.html)

### Обучающие материалы
- [Kotlin Coroutines Tutorial](https://www.baeldung.com/kotlin/coroutines)
- [Kotlin Flow Tutorial](https://www.baeldung.com/kotlin/flow)

### См. также
- [[kotlin-basics|Основы Kotlin]]
- [[kotlin-concurrency-basics|Основы корутин]]
- [[kotlin-reactive|Реактивное программирование]]

## Содержание

- [Coroutine Context и Dispatchers (детально)](#coroutine-context-и-dispatchers-детально)
  - [CoroutineContext — детальное понимание](#coroutinecontext-детальное-понимание)
  - [Композиция контекста](#композиция-контекста)
  - [Dispatchers — детальное использование](#dispatchers-детальное-использование)
  - [Переключение Dispatchers](#переключение-dispatchers)
  - [CoroutineName](#coroutinename)
  - [ExceptionHandler в контексте](#exceptionhandler-в-контексте)
- [Structured Concurrency](#structured-concurrency)
  - [Принципы Structured Concurrency](#принципы-structured-concurrency)
  - [Отмена в Structured Concurrency](#отмена-в-structured-concurrency)
  - [SupervisorScope](#supervisorscope)
  - [Иерархия корутин](#иерархия-корутин)
- [Coroutine Scopes](#coroutine-scopes)
  - [GlobalScope](#globalscope)
  - [CoroutineScope](#coroutinescope)
  - [LifecycleScope (Android)](#lifecyclescope-android)
  - [ViewModelScope (Android)](#viewmodelscope-android)
- [Job и Deferred](#job-и-deferred)
  - [Job — детальное использование](#job-детальное-использование)
  - [Job иерархия](#job-иерархия)
  - [SupervisorJob](#supervisorjob)
  - [Deferred — результат async](#deferred-результат-async)
  - [Deferred с обработкой ошибок](#deferred-с-обработкой-ошибок)
  - [awaitAll](#awaitall)
- [Flow (продвинутое)](#flow-продвинутое)
  - [Cold vs Hot Flows](#cold-vs-hot-flows)
  - [Flow Operators (продвинутые)](#flow-operators-продвинутые)
  - [Backpressure (обратное давление)](#backpressure-обратное-давление)
  - [StateFlow](#stateflow)
  - [SharedFlow](#sharedflow)
  - [Flow с контекстом](#flow-с-контекстом)
- [Channel (продвинутое)](#channel-продвинутое)
  - [Типы каналов](#типы-каналов)
  - [Channel операторы](#channel-операторы)
  - [BroadcastChannel](#broadcastchannel)
  - [Fan-out и Fan-in](#fan-out-и-fan-in)
  - [Select expression](#select-expression)
- [Mutex и Semaphore](#mutex-и-semaphore)
  - [Mutex](#mutex)
  - [Semaphore](#semaphore)
  - [ReadWriteMutex (через кастомную реализацию)](#readwritemutex-через-кастомную-реализацию)
- [Atomic Operations](#atomic-operations)
  - [Atomic типы](#atomic-типы)
  - [CAS операции](#cas-операции)
- [SharedState и Concurrency](#sharedstate-и-concurrency)
  - [Проблемы с общим состоянием](#проблемы-с-общим-состоянием)
  - [Решения](#решения)
  - [Thread-safe коллекции](#thread-safe-коллекции)
- [Testing Coroutines](#testing-coroutines)
  - [TestCoroutineDispatcher](#testcoroutinedispatcher)
  - [TestCoroutineScope](#testcoroutinescope)
  - [Тестирование Flow](#тестирование-flow)
  - [Мокирование корутин](#мокирование-корутин)
- [Лучшие практики](#лучшие-практики)
  - [Избегайте GlobalScope](#избегайте-globalscope)
  - [Используйте Structured Concurrency](#используйте-structured-concurrency)
  - [Правильный выбор Dispatcher](#правильный-выбор-dispatcher)
  - [Обработка ошибок](#обработка-ошибок)
  - [Отмена корутин](#отмена-корутин)
- [Производительность корутин](#производительность-корутин)
  - [Оптимизация производительности](#оптимизация-производительности)
  - [Профилирование корутин](#профилирование-корутин)
- [Работа с внешними библиотеками](#работа-с-внешними-библиотеками)
  - [Интеграция с Retrofit](#интеграция-с-retrofit)
  - [Интеграция с Room](#интеграция-с-room)
- [Продвинутые паттерны](#продвинутые-паттерны)
  - [Pipeline Pattern](#pipeline-pattern)
  - [Worker Pool Pattern](#worker-pool-pattern)
- [Мониторинг и отладка](#мониторинг-и-отладка)
  - [Мониторинг корутин](#мониторинг-корутин)
  - [Логирование корутин](#логирование-корутин)
- [Продвинутые техники работы с Flow](#продвинутые-техники-работы-с-flow)
  - [Создание пользовательских операторов Flow](#создание-пользовательских-операторов-flow)
  - [Работа с несколькими Flow одновременно](#работа-с-несколькими-flow-одновременно)
- [Производительность и оптимизация корутин](#производительность-и-оптимизация-корутин)
  - [Оптимизация использования памяти](#оптимизация-использования-памяти)
  - [Оптимизация производительности корутин](#оптимизация-производительности-корутин)
- [Дополнительные техники Flow](#дополнительные-техники-flow)
  - [Работа с StateFlow и SharedFlow](#работа-с-stateflow-и-sharedflow)
  - [Работа с Hot и Cold Flow](#работа-с-hot-и-cold-flow)
- [Дополнительные техники конкурентности](#дополнительные-техники-конкурентности)
  - [Работа с SupervisorJob](#работа-с-supervisorjob)
  - [Работа с Channel и Flow вместе](#работа-с-channel-и-flow-вместе)
- [Дополнительные техники](#дополнительные-техники)
  - [Работа с корутинами и Actor Model](#работа-с-корутинами-и-actor-model)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Итоговые рекомендации](#итоговые-рекомендации)
- [Практические примеры использования](#практические-примеры-использования)
  - [Координация нескольких Flow](#координация-нескольких-flow)
  - [Использование SupervisorJob для независимых корутин](#использование-supervisorjob-для-независимых-корутин)
  - [Использование Mutex для синхронизации](#использование-mutex-для-синхронизации)

## Coroutine Context и Dispatchers (детально)

**CoroutineContext** является фундаментальной концепцией в **Kotlin Coroutines**. Он определяет окружение, в котором выполняется корутина, включая поток выполнения, имя корутины, обработчик исключений и другие элементы. Понимание **CoroutineContext** критично для правильной работы с корутинами.

### CoroutineContext — детальное понимание

**CoroutineContext** представляет собой набор элементов, которые определяют поведение корутины. Каждый элемент контекста отвечает за определенный аспект выполнения: **Dispatcher** определяет поток, **CoroutineName** — имя для отладки, **Job** — жизненный цикл корутины, **ExceptionHandler** — обработку ошибок.

Контекст можно комбинировать через оператор `+`, что позволяет создавать сложные конфигурации. Элементы контекста имеют приоритеты, и при объединении более поздние элементы могут переопределять более ранние.

```kotlin
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.*

// CoroutineContext - это набор элементов
val context: CoroutineContext = Dispatchers.Default + CoroutineName("MyCoroutine")
```

В этом примере создается контекст, который содержит **Dispatcher** для выполнения на фоновом потоке и имя корутины для отладки. Комбинирование элементов позволяет создавать специализированные контексты для разных типов задач.

// Элементы контекста
**val dispatcher** = **context**[**ContinuationInterceptor**]  // **Dispatcher**
**val name** = **context**[**CoroutineName**]                  // **CoroutineName**
**val job** = **context**[**Job**]                              // **Job**
```text

### Композиция контекста

```kotlin
// Объединение контекстов
val context1 = `Dispatchers`.`Default`
val context2 = `CoroutineName`("`MyCoroutine`")
val combined = context1 + context2

// Переопределение элементов
val base = `Dispatchers`.`Default` + `CoroutineName`("`Base`")
val overridden = base + `CoroutineName`("`Overridden`") // CoroutineName переопределен

// Удаление элементов
val `withoutName` = combined.`minusKey`(`CoroutineName`.Key)
```text

### Dispatchers - детальное использование

```kotlin
import `kotlinx.coroutines`.*

// `Dispatchers`.`Default` - для `CPU`-интенсивных задач
suspend fun `cpuIntensive()` = `withContext`(`Dispatchers`.`Default`) {
    // Вычисления
}

// `Dispatchers`.`IO` - для I/O операций
suspend fun `ioOperation()` = `withContext`(`Dispatchers`.`IO`) {
    // Чтение/запись файлов, сетевые запросы
}

// `Dispatchers`.`Main` - для `UI` операций (`Android`, `JavaFX`)
suspend fun `updateUI()` = `withContext`(`Dispatchers`.`Main`) {
    // Обновление `UI`
}

// `Dispatchers`.`Unconfined` - не привязан к потоку
suspend fun unconfined() = `withContext`(`Dispatchers`.`Unconfined`) {
    // Выполняется в текущем потоке до первой точки приостановки
}

// Кастомный `Dispatcher`
val `customDispatcher` = `Executors`.`newFixedThreadPool`(4).`asCoroutineDispatcher()`
```text

### Переключение Dispatchers

```kotlin
suspend fun example() {
    // Начинаем в `Main`
    `withContext`(`Dispatchers`.`Main`) {
        println("`Main`: ${`Thread`.`currentThread()`.name}")

        // Переключаемся на `IO`
        `withContext`(`Dispatchers`.`IO`) {
            println("`IO`: ${`Thread`.`currentThread()`.name}")

            // Переключаемся на `Default`
            `withContext`(`Dispatchers`.`Default`) {
                println("`Default`: ${`Thread`.`currentThread()`.name}")
            }
        }

        // Возвращаемся в `Main`
        println("`Main again`: ${`Thread`.`currentThread()`.name}")
    }
}
```text

### CoroutineName

```kotlin
// Именование корутин для отладки
val job = `CoroutineScope`(`Dispatchers`.`Default` + `CoroutineName`("`DataProcessing`")).launch {
    println(`coroutineContext`[`CoroutineName`]) // CoroutineName(DataProcessing)
}

// Изменение имени в контексте
suspend fun process() {
    `withContext`(`CoroutineName`("`Processing`")) {
        println(`coroutineContext`[`CoroutineName`]) // CoroutineName(Processing)
    }
}
```text

### ExceptionHandler в контексте

```kotlin
val `exceptionHandler` = `CoroutineExceptionHandler` { context, exception ->
    println("`Caught exception`: $exception in context: $context")
}

val scope = `CoroutineScope`(`Dispatchers`.`Default` + `exceptionHandler`)

`scope.launch` {
    throw `RuntimeException`("`Test exception`")
}
```text

## Structured Concurrency

Structured Concurrency (структурированная конкурентность) - это принцип организации корутин, который гарантирует, что все дочерние корутины завершаются до завершения родительской корутины. Это предотвращает утечки корутин и обеспечивает предсказуемое управление жизненным циклом.

Идея структурированной конкурентности заключается в создании иерархии корутин, где родительская корутина отвечает за жизненный цикл всех дочерних корутин. Если родительская корутина отменяется, все дочерние корутины также отменяются автоматически. Это делает управление корутинами более безопасным и предсказуемым.

### Принципы Structured Concurrency

Основной принцип структурированной конкурентности - это создание scope для корутин, который автоматически управляет их жизненным циклом. Функция `coroutineScope` создает такой scope и гарантирует, что все запущенные в нем корутины завершатся до выхода из функции.

```kotlin
import `kotlinx.coroutines`.*

// Структурированная конкурентность
suspend fun `structuredExample()` = `coroutineScope` {
    launch {
        delay(`1000`)
        println("`Child 1`")
    }

    launch {
        delay(`1000`)
        println("`Child 2`")
    }

    // Родительская корутина ждет завершения всех дочерних
    println("`Parent waiting`")
}
```text

В этом примере `coroutineScope` создает scope, который ждет завершения всех дочерних корутин перед выходом из функции. Это гарантирует, что все асинхронные операции завершатся, и ресурсы будут освобождены корректно. Если любая из дочерних корутин выбросит исключение, оно будет распространено на родительскую корутину, что обеспечивает правильную обработку ошибок.

// Все дочерние корутины завершаются до выхода из coroutineScope
```

### Отмена в Structured Concurrency

```kotlin
suspend fun cancellationExample() = coroutineScope {
    val job1 = launch {
        try {
            delay(5000)
            println("Job 1 completed")
        } catch (e: CancellationException) {
            println("Job 1 cancelled")
            throw e
        }
    }

    val job2 = launch {
        delay(1000)
        println("Job 2 completed")
    }

    delay(2000)
    job1.cancel()  // Отмена одной корутины

    // job2 продолжит выполнение
}
```

### SupervisorScope

**SupervisorScope** позволяет дочерним корутинам завершаться независимо.

```kotlin
suspend fun supervisorExample() = supervisorScope {
    val job1 = launch {
        delay(1000)
        throw RuntimeException("Error in job1")
    }

    val job2 = launch {
        delay(2000)
        println("Job 2 completed")
    }

    // job1 падает, но job2 продолжает выполнение
    joinAll(job1, job2)
}
```

### Иерархия корутин

```kotlin
suspend fun hierarchyExample() = coroutineScope {
    println("Root coroutine")

    launch {
        println("Child 1")

        launch {
            println("Grandchild 1")
        }

        launch {
            println("Grandchild 2")
        }
    }

    launch {
        println("Child 2")
    }

    // Все дочерние и внучатые корутины завершаются
}
```

## Coroutine Scopes

### GlobalScope

```kotlin
import kotlinx.coroutines.*

// GlobalScope - живет все время работы приложения
fun globalScopeExample() {
    GlobalScope.launch {
        delay(1000)
        println("GlobalScope coroutine")
    }

    // Проблема: корутина может пережить компонент, который ее создал
}

// Когда использовать GlobalScope
// - Только для корутин, которые должны жить все время работы приложения
// - В основном для логирования, мониторинга
```

### CoroutineScope

```kotlin
// Создание собственного scope
class MyComponent {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    fun doWork() {
        scope.launch {
            // Работа
        }
    }

    fun cleanup() {
        scope.cancel()  // Отмена всех корутин в scope
    }
}
```

### SupervisorScope

```kotlin
suspend fun supervisorScopeExample() = supervisorScope {
    val job1 = launch {
        delay(1000)
        throw RuntimeException("Error")
    }

    val job2 = launch {
        delay(2000)
        println("Job 2 completed")
    }

    // job1 падает, но не отменяет job2
    try {
        job1.join()
    } catch (e: Exception) {
        println("Job 1 failed: ${e.message}")
    }

    job2.join()
}
```

### LifecycleScope (Android)

```kotlin
// Пример для Android
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LifecycleScope автоматически отменяется при уничтожении Activity
        lifecycleScope.launch {
            // Работа
        }
    }
}
```

### ViewModelScope (Android)

```kotlin
// Пример для Android ViewModel
class MyViewModel : ViewModel() {
    fun loadData() {
        viewModelScope.launch {
            // Работа, автоматически отменяется при очистке ViewModel
        }
    }
}
```

## Job и Deferred

### Job — детальное использование

```kotlin
import kotlinx.coroutines.*

// Job представляет корутину
val job: Job = launch {
    delay(1000)
    println("Completed")
}

// Состояния Job
println(job.isActive)      // true
println(job.isCompleted)   // false
println(job.isCancelled)   // false

// Ожидание завершения
job.join()

// Отмена
job.cancel()

// Проверка состояния после отмены
println(job.isCancelled)   // true
```

### Job иерархия

```kotlin
suspend fun jobHierarchy() = coroutineScope {
    val parentJob = launch {
        val childJob1 = launch {
            delay(1000)
            println("Child 1")
        }

        val childJob2 = launch {
            delay(2000)
            println("Child 2")
        }

        // Отмена родителя отменяет всех детей
    }

    delay(500)
    parentJob.cancel()  // Отменяет parentJob, childJob1, childJob2
}
```

### SupervisorJob

```kotlin
val supervisor = SupervisorJob()
val scope = CoroutineScope(Dispatchers.Default + supervisor)

scope.launch {
    throw RuntimeException("Error")
    // Не отменяет другие корутины в scope
}

scope.launch {
    delay(1000)
    println("This will complete")
}
```

### Deferred — результат async

```kotlin
suspend fun deferredExample() = coroutineScope {
    // Deferred - это Job с результатом
    val deferred1: Deferred<Int> = async {
        delay(1000)
        42
    }

    val deferred2: Deferred<String> = async {
        delay(2000)
        "Hello"
    }

    // Ожидание результата
    val result1 = deferred1.await()  // 42
    val result2 = deferred2.await()  // "Hello"

    println("$result1, $result2")
}
```

### Deferred с обработкой ошибок

```kotlin
suspend fun deferredErrorHandling() = coroutineScope {
    val deferred: Deferred<Int> = async {
        delay(1000)
        throw RuntimeException("Error")
    }

    try {
        val result = deferred.await()
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
}
```

### awaitAll

```kotlin
suspend fun awaitAllExample() = coroutineScope {
    val deferreds = listOf(
        async { delay(1000); 1 },
        async { delay(2000); 2 },
        async { delay(3000); 3 }
    )

    val results = deferreds.awaitAll()  // [1, 2, 3]
    println(results)
}
```

## Flow (продвинутое)

### Cold vs Hot Flows

```kotlin
import kotlinx.coroutines.flow.*

// Cold Flow - создается при каждом collect
fun coldFlow(): Flow<Int> = flow {
    println("Flow started")
    repeat(3) {
        emit(it)
        delay(100)
    }
}

// Каждый collect создает новый поток
runBlocking {
    coldFlow().collect { println(it) }  // Flow started, 0, 1, 2
    coldFlow().collect { println(it) }  // Flow started, 0, 1, 2 (снова)
}

// Hot Flow - StateFlow, SharedFlow
val stateFlow = MutableStateFlow(0)

// Все коллекторы получают одно и то же значение
runBlocking {
    stateFlow.collect { println("Collector 1: $it") }
    stateFlow.collect { println("Collector 2: $it") }
}
```

### Flow Operators (продвинутые)

```kotlin
// transform - кастомная трансформация
fun transformExample(): Flow<String> = flowOf(1, 2, 3)
    .transform { value ->
        emit("Value: $value")
        emit("Double: ${value * 2}")
    }

// flatMapLatest - отменяет предыдущий flow при новом значении
fun flatMapLatestExample(): Flow<String> = flowOf(1, 2, 3)
    .flatMapLatest { value ->
        flow {
            delay(100)
            emit("Processed: $value")
        }
    }

// debounce - пропускает значения, если следующее приходит слишком быстро
fun debounceExample(): Flow<Int> = flow {
    emit(1)
    delay(100)
    emit(2)
    delay(50)
    emit(3)
    delay(200)
    emit(4)
}.debounce(150)  // Пропустит 2 и 3, оставит 1 и 4

// sample - берет последнее значение за период
fun sampleExample(): Flow<Int> = flow {
    repeat(10) {
        emit(it)
        delay(50)
    }
}.sample(200)  // Берет значение каждые 200ms

// distinctUntilChanged - пропускает повторяющиеся значения
fun distinctExample(): Flow<Int> = flowOf(1, 1, 2, 2, 3, 1)
    .distinctUntilChanged()  // 1, 2, 3, 1
```

### Backpressure (обратное давление)

```kotlin
// buffer - буферизует значения
fun bufferExample(): Flow<Int> = flow {
    repeat(5) {
        emit(it)
        delay(100)
    }
}.buffer(capacity = 3)  // Буфер на 3 элемента

// conflate - сохраняет только последнее значение
fun conflateExample(): Flow<Int> = flow {
    repeat(10) {
        emit(it)
        delay(50)
    }
}.conflate()  // Пропускает промежуточные значения

// collectLatest - отменяет предыдущую обработку
suspend fun collectLatestExample() {
    flow {
        repeat(10) {
            emit(it)
            delay(100)
        }
    }.collectLatest { value ->
        delay(200)  // Если новое значение придет, эта обработка отменится
        println(value)
    }
}
```

### StateFlow

```kotlin
import kotlinx.coroutines.flow.*

// StateFlow - hot flow с текущим состоянием
val stateFlow = MutableStateFlow(0)

// Обновление значения
stateFlow.value = 1
stateFlow.update { it + 1 }

// Подписка
stateFlow.collect { value ->
    println("State: $value")
}

// StateFlow всегда имеет значение
println(stateFlow.value)  // Текущее значение

// Сравнение значений
stateFlow.compareAndSet(1, 2)  // Обновляет только если текущее значение = 1
```

### SharedFlow

```kotlin
// SharedFlow - hot flow без начального значения
val sharedFlow = MutableSharedFlow<Int>(
    replay = 2,           // Количество последних значений для новых подписчиков
    extraBufferCapacity = 1  // Дополнительный буфер
)

// Эмиссия значений
sharedFlow.emit(1)
sharedFlow.emit(2)
sharedFlow.emit(3)

// Подписка
sharedFlow.collect { value ->
    println("Received: $value")
}

// tryEmit - не приостанавливается
sharedFlow.tryEmit(4)  // true если успешно, false если буфер полон
```

### Flow с контекстом

```kotlin
// flowOn - меняет контекст выполнения
fun flowOnExample(): Flow<Int> = flow {
    repeat(5) {
        emit(it)
        delay(100)
    }
}.flowOn(Dispatchers.IO)  // Выполняется на IO dispatcher

// catch - обработка ошибок
fun catchExample(): Flow<Int> = flow {
    emit(1)
    throw RuntimeException("Error")
    emit(2)
}.catch { e ->
    println("Caught: ${e.message}")
    emit(-1)  // Значение по умолчанию
}

// retry - повтор при ошибке
fun retryExample(): Flow<Int> = flow {
    emit(1)
    throw RuntimeException("Error")
}.retry(3) { e ->
    e is RuntimeException
}
```

## Channel (продвинутое)

### Типы каналов

```kotlin
import kotlinx.coroutines.channels.*

// Rendezvous channel (по умолчанию) - без буфера
val rendezvousChannel = Channel<Int>()

// Buffered channel - с буфером
val bufferedChannel = Channel<Int>(10)

// Unlimited channel - неограниченный буфер
val unlimitedChannel = Channel<Int>(Channel.UNLIMITED)

// Conflated channel - сохраняет только последнее значение
val conflatedChannel = Channel<Int>(Channel.CONFLATED)
```

### Channel операторы

```kotlin
// produce - создает канал и корутину
fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
    repeat(5) {
        send(it)
        delay(100)
    }
}

// consumeEach - итерация по каналу
suspend fun consumeExample() {
    val channel = produceNumbers()
    channel.consumeEach { value ->
        println("Received: $value")
    }
}

// consume - автоматическое закрытие
suspend fun consumeExample() {
    produceNumbers().consumeEach { value ->
        println("Received: $value")
    }  // Канал автоматически закрывается
}
```

### BroadcastChannel

```kotlin
// BroadcastChannel - один отправитель, много получателей
val broadcastChannel = BroadcastChannel<Int>(10)

// Подписка
val receiver1 = broadcastChannel.openSubscription()
val receiver2 = broadcastChannel.openSubscription()

// Отправка
broadcastChannel.send(1)
broadcastChannel.send(2)

// Получение
receiver1.receive()  // 1
receiver2.receive()  // 1 (оба получают одно значение)
```

### Fan-out и Fan-in

```kotlin
// Fan-out - один производитель, много потребителей
suspend fun fanOut() = coroutineScope {
    val channel = produceNumbers()

    repeat(3) { id ->
        launch {
            channel.consumeEach { value ->
                println("Consumer $id: $value")
            }
        }
    }
}

// Fan-in - много производителей, один потребитель
suspend fun fanIn() = coroutineScope {
    val channel = Channel<Int>()

    // Много производителей
    repeat(3) { id ->
        launch {
            repeat(5) {
                channel.send(id * 10 + it)
                delay(100)
            }
        }
    }

    // Один потребитель
    launch {
        repeat(15) {
            println("Received: ${channel.receive()}")
        }
    }
}
```

### Select expression

```kotlin
import kotlinx.coroutines.selects.*

// select - выбор первого доступного канала
suspend fun selectExample() = coroutineScope {
    val channel1 = produce { repeat(5) { send(it); delay(100) } }
    val channel2 = produce { repeat(5) { send(it * 10); delay(150) } }

    repeat(10) {
        select<Unit> {
            channel1.onReceive { value ->
                println("Channel1: $value")
            }
            channel2.onReceive { value ->
                println("Channel2: $value")
            }
        }
    }
}

// select с onAwait для Deferred
suspend fun selectDeferred() = coroutineScope {
    val deferred1 = async { delay(100); 1 }
    val deferred2 = async { delay(200); 2 }

    val result = select<Int> {
        deferred1.onAwait { it }
        deferred2.onAwait { it }
    }

    println("First result: $result")
}
```

## Mutex и Semaphore

### Mutex

```kotlin
import kotlinx.coroutines.sync.*

// Mutex - взаимное исключение
val mutex = Mutex()

suspend fun mutexExample() = coroutineScope {
    var counter = 0

    repeat(100) {
        launch {
            mutex.withLock {
                counter++
            }
        }
    }

    delay(1000)
    println("Counter: $counter")  // 100
}

// withLock - автоматическое освобождение
suspend fun withLockExample() {
    mutex.withLock {
        // Критическая секция
        // Mutex автоматически освобождается при выходе
    }
}

// lock/unlock вручную
suspend fun manualLock() {
    mutex.lock()
    try {
        // Критическая секция
    } finally {
        mutex.unlock()
    }
}
```

### Semaphore

```kotlin
import kotlinx.coroutines.sync.*

// Semaphore - ограничение количества одновременных операций
val semaphore = Semaphore(permits = 3)

suspend fun semaphoreExample() = coroutineScope {
    repeat(10) {
        launch {
            semaphore.withPermit {
                println("Task $it started")
                delay(1000)
                println("Task $it completed")
            }
        }
    }
}

// Только 3 задачи выполняются одновременно
```

### ReadWriteMutex (через кастомную реализацию)

```kotlin
// Kotlin не имеет встроенного ReadWriteMutex, но можно использовать Semaphore
class ReadWriteLock {
    private val readSemaphore = Semaphore(Int.MAX_VALUE)
    private val writeMutex = Mutex()

    suspend fun <T> read(block: suspend () -> T): T {
        readSemaphore.acquire()
        try {
            return block()
        } finally {
            readSemaphore.release()
        }
    }

    suspend fun <T> write(block: suspend () -> T): T {
        writeMutex.withLock {
            readSemaphore.acquire(Int.MAX_VALUE)
            try {
                return block()
            } finally {
                readSemaphore.release(Int.MAX_VALUE)
            }
        }
    }
}
```

## Atomic Operations

### Atomic типы

```kotlin
import java.util.concurrent.atomic.*

// AtomicInteger
val atomicInt = AtomicInteger(0)

fun atomicExample() {
    atomicInt.incrementAndGet()  // Атомарное увеличение
    atomicInt.addAndGet(5)       // Атомарное добавление
    atomicInt.compareAndSet(5, 10)  // CAS операция
}

// AtomicReference
val atomicRef = AtomicReference<String>("initial")

fun atomicRefExample() {
    atomicRef.set("new value")
    atomicRef.compareAndSet("initial", "updated")
    val value = atomicRef.get()
}

// AtomicLong
val atomicLong = AtomicLong(0L)

fun atomicLongExample() {
    atomicLong.incrementAndGet()
    atomicLong.addAndGet(100L)
}
```

### CAS операции

```kotlin
// Compare-And-Swap
val atomicInt = AtomicInteger(0)

fun casExample() {
    var current: Int
    var new: Int
    do {
        current = atomicInt.get()
        new = current + 1
    } while (!atomicInt.compareAndSet(current, new))
}

// updateAndGet
val result = atomicInt.updateAndGet { it + 1 }

// getAndUpdate
val oldValue = atomicInt.getAndUpdate { it + 1 }
```

## SharedState и Concurrency

### Проблемы с общим состоянием

```kotlin
// Проблема: race condition
var counter = 0

suspend fun raceConditionExample() = coroutineScope {
    repeat(1000) {
        launch {
            counter++  // Не атомарная операция!
        }
    }
    delay(1000)
    println("Counter: $counter")  // Может быть меньше 1000!
}
```

### Решения

```kotlin
// Решение 1: Atomic
val atomicCounter = AtomicInteger(0)

suspend fun atomicSolution() = coroutineScope {
    repeat(1000) {
        launch {
            atomicCounter.incrementAndGet()
        }
    }
    delay(1000)
    println("Counter: ${atomicCounter.get()}")
}

// Решение 2: Mutex
val mutex = Mutex()
var counter = 0

suspend fun mutexSolution() = coroutineScope {
    repeat(1000) {
        launch {
            mutex.withLock {
                counter++
            }
        }
    }
    delay(1000)
    println("Counter: $counter")
}

// Решение 3: Actor (через Channel)
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

suspend fun actorSolution() = coroutineScope {
    val counter = counterActor()

    repeat(1000) {
        launch {
            counter.send(IncCounter)
        }
    }

    delay(1000)
    val response = CompletableDeferred<Int>()
    counter.send(GetCounter(response))
    println("Counter: ${response.await()}")
}
```

### Thread-safe коллекции

```kotlin
import java.util.concurrent.*

// ConcurrentHashMap
val map = ConcurrentHashMap<String, Int>()

suspend fun concurrentMapExample() = coroutineScope {
    repeat(100) {
        launch {
            map["key"] = map.getOrDefault("key", 0) + 1
        }
    }
    delay(1000)
    println("Value: ${map["key"]}")
}

// CopyOnWriteArrayList
val list = CopyOnWriteArrayList<Int>()

suspend fun concurrentListExample() = coroutineScope {
    repeat(100) {
        launch {
            list.add(it)
        }
    }
    delay(1000)
    println("Size: ${list.size}")
}
```

## Testing Coroutines

### TestCoroutineDispatcher

```kotlin
import kotlinx.coroutines.test.*

// TestCoroutineDispatcher - контролирует время выполнения
fun testExample() {
    val testDispatcher = TestCoroutineDispatcher()

    runBlockingTest(testDispatcher) {
        val job = launch {
            delay(1000)
            println("Delayed")
        }

        advanceTimeBy(1000)  // Пропускаем время
        job.join()
    }
}

// runBlockingTest
fun testWithRunBlockingTest() = runBlockingTest {
    val deferred = async {
        delay(1000)
        42
    }

    advanceTimeBy(1000)
    assertEquals(42, deferred.await())
}
```

### TestCoroutineScope

```kotlin
fun testWithScope() {
    val testScope = TestCoroutineScope()

    testScope.launch {
        delay(1000)
        println("Test")
    }

    testScope.advanceTimeBy(1000)
    testScope.cleanupTestCoroutines()
}
```

### Тестирование Flow

```kotlin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*

fun testFlow() = runBlockingTest {
    val flow = flow {
        emit(1)
        delay(100)
        emit(2)
        delay(100)
        emit(3)
    }

    val results = flow.take(2).toList()
    assertEquals(listOf(1, 2), results)
}
```

### Мокирование корутин

```kotlin
// Использование TestCoroutineDispatcher для мокирования
class Repository(private val dispatcher: CoroutineDispatcher) {
    suspend fun fetchData(): String = withContext(dispatcher) {
        delay(1000)
        "Data"
    }
}

fun testRepository() = runBlockingTest {
    val testDispatcher = TestCoroutineDispatcher()
    val repository = Repository(testDispatcher)

    val result = repository.fetchData()
    assertEquals("Data", result)
}
```

## Лучшие практики

### Избегайте GlobalScope

```kotlin
// Плохо
fun badExample() {
    GlobalScope.launch {
        // Работа
    }
}

// Хорошо
class MyComponent {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun goodExample() {
        scope.launch {
            // Работа
        }
    }

    fun cleanup() {
        scope.cancel()
    }
}
```

### Используйте Structured Concurrency

```kotlin
// Плохо
suspend fun badExample() {
    launch { /* ... */ }
    launch { /* ... */ }
    // Нет гарантии, что все завершатся
}

// Хорошо
suspend fun goodExample() = coroutineScope {
    launch { /* ... */ }
    launch { /* ... */ }
    // Все гарантированно завершатся
}
```

### Правильный выбор Dispatcher

```kotlin
// CPU-интенсивные задачи
suspend fun cpuTask() = withContext(Dispatchers.Default) {
    // Вычисления
}

// I/O операции
suspend fun ioTask() = withContext(Dispatchers.IO) {
    // Чтение/запись
}

// UI операции
suspend fun uiTask() = withContext(Dispatchers.Main) {
    // Обновление UI
}
```

### Обработка ошибок

```kotlin
// Используйте CoroutineExceptionHandler
val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    println("Caught: $exception")
}

val scope = CoroutineScope(Dispatchers.Default + exceptionHandler)

// Используйте try-catch в корутинах
scope.launch {
    try {
        // Работа
    } catch (e: Exception) {
        // Обработка
    }
}
```

### Отмена корутин

```kotlin
// Всегда проверяйте isActive
suspend fun cancellableWork() {
    while (isActive) {
        // Работа
        delay(100)
    }
}

// Используйте ensureActive()
suspend fun ensureActiveExample() {
    repeat(1000) {
        ensureActive()  // Выбрасывает CancellationException если отменено
        // Работа
    }
}
```

Этот файл содержит продвинутые темы по конкурентности в **Kotlin**, дополняя базовый файл более глубокими концепциями и практиками.

## Производительность корутин

### Оптимизация производительности

**Оптимизация производительности корутин критична для создания эффективных приложений:**

```kotlin
// Использование правильного dispatcher
suspend fun optimizeWithDispatcher() = withContext(Dispatchers.Default) {
    // CPU-интенсивные операции
    computeHeavyTask()
}

// Batch обработка для улучшения производительности
suspend fun batchProcessing(items: List<Item>) {
    items.chunked(100).forEach { chunk ->
        coroutineScope {
            chunk.forEach { item ->
                launch {
                    processItem(item)
                }
            }
        }
    }
}
```

Правильный выбор **dispatcher** и **batch** обработка могут значительно улучшить производительность приложений с корутинами.

### Профилирование корутин

**Профилирование помогает выявить узкие места в производительности:**

```kotlin
import kotlinx.coroutines.debug.CoroutineName

suspend fun profileCoroutines() {
    val startTime = System.nanoTime()

    coroutineScope {
        launch(CoroutineName("Task1")) {
            task1()
        }
        launch(CoroutineName("Task2")) {
            task2()
        }
    }

    val duration = System.nanoTime() - startTime
    println("Total time: ${duration / 1_000_000}ms")
}

// Использование CoroutineName для отладки
val job = launch(CoroutineName("MyCoroutine")) {
    // Работа
}
```

Профилирование и использование именованных корутин помогают выявлять проблемы производительности и упрощают отладку.

## Работа с внешними библиотеками

### Интеграция с Retrofit

**Корутины отлично интегрируются с **Retrofit** для работы с **API**:**

```kotlin
interface ApiService {
    @GET("/users/{id}")
    suspend fun getUser(@Path("id") id: Long): User

    @POST("/users")
    suspend fun createUser(@Body user: User): User
}

class UserRepository(private val api: ApiService) {
    suspend fun getUser(id: Long): Result<User> {
        return try {
            Result.success(api.getUser(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

Интеграция с **Retrofit** позволяет использовать корутины для работы с **REST API**, что делает код более читаемым и эффективным.

### Интеграция с Room

**Корутины интегрируются с **Room** для работы с базами данных:**

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}

class UserRepository(private val dao: UserDao) {
    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        dao.getAllUsers()
    }
}
```

Использование корутин с **Room** упрощает работу с базами данных и обеспечивает неблокирующие операции.

## Продвинутые паттерны

### Pipeline Pattern

**Pipeline Pattern** позволяет обрабатывать данные через цепочку корутин:**

```kotlin
suspend fun pipelineExample() = coroutineScope {
    val input = Channel<Int>(Channel.UNLIMITED)
    val stage1 = Channel<Int>(Channel.UNLIMITED)
    val stage2 = Channel<String>(Channel.UNLIMITED)

    // Stage 1: Фильтрация
    launch {
        for (value in input) {
            if (value > 0) {
                stage1.send(value * 2)
            }
        }
        stage1.close()
    }

    // Stage 2: Трансформация
    launch {
        for (value in stage1) {
            stage2.send("Value: $value")
        }
        stage2.close()
    }

    // Отправка данных
    launch {
        repeat(10) {
            input.send(it)
        }
        input.close()
    }

    // Получение результатов
    for (result in stage2) {
        println(result)
    }
}
```

**Pipeline Pattern** позволяет создавать эффективные цепочки обработки данных с параллельной обработкой на каждом этапе.

### Worker Pool Pattern

**Worker Pool Pattern** позволяет распределять работу между несколькими воркерами:**

```kotlin
suspend fun workerPoolExample(items: List<Item>) = coroutineScope {
    val workChannel = Channel<Item>(Channel.UNLIMITED)
    val resultChannel = Channel<Result>(Channel.UNLIMITED)

    // Создание воркеров
    repeat(4) {
        launch {
            for (item in workChannel) {
                val result = processItem(item)
                resultChannel.send(result)
            }
        }
    }

    // Отправка работы
    launch {
        items.forEach { workChannel.send(it) }
        workChannel.close()
    }

    // Сбор результатов
    val results = mutableListOf<Result>()
    for (i in items.indices) {
        results.add(resultChannel.receive())
    }
    resultChannel.close()

    results
}
```

**Worker Pool Pattern** эффективно распределяет нагрузку между несколькими корутинами, что улучшает производительность для **CPU**-интенсивных задач.

## Мониторинг и отладка

### Мониторинг корутин

**Мониторинг корутин помогает отслеживать состояние приложения:**

```kotlin
import kotlinx.coroutines.debug.CoroutineName
import kotlinx.coroutines.debug.DebugProbes

fun enableCoroutineMonitoring() {
    DebugProbes.install()
}

suspend fun monitorCoroutines() {
    val job = launch(CoroutineName("MonitoredCoroutine")) {
        delay(1000)
        // Работа
    }

    // Получение информации о корутинах
    val dump = DebugProbes.dumpCoroutinesInfo()
    dump.forEach { info ->
        println("Coroutine: ${info.name}, State: ${info.state}")
    }
}
```

Мониторинг корутин позволяет отслеживать состояние выполнения и выявлять проблемы в многопоточном коде.

### Логирование корутин

**Логирование корутин помогает отлаживать асинхронный код:**

```kotlin
import kotlinx.coroutines.debug.CoroutineName
import kotlin.coroutines.coroutineContext

suspend fun logCurrentCoroutine() {
    val name = coroutineContext[CoroutineName]?.name ?: "Unnamed"
    println("Executing in coroutine: $name")
}

fun setupLogging() {
    // Настройка логирования корутин
    System.setProperty("kotlinx.coroutines.debug", "on")
}

suspend fun loggedOperation() {
    logCurrentCoroutine()
    // Работа
}
```

Логирование корутин упрощает отладку и понимание потока выполнения в асинхронном коде.

Этот файл содержит полное руководство по продвинутой конкурентности в **Kotlin**, покрывающее все основные аспекты от оптимизации производительности до интеграции с внешними библиотеками и продвинутых паттернов.

## Продвинутые техники работы с Flow

### Создание пользовательских операторов Flow

**Создание пользовательских операторов для **Flow**:**

```kotlin
// Пользовательский оператор для debounce с условием
fun <T> Flow<T>.debounceIf(
    timeout: Long,
    condition: (T) -> Boolean
): Flow<T> = flow {
    var lastValue: T? = null
    var lastEmitTime = 0L

    collect { value ->
        val now = System.currentTimeMillis()
        if (condition(value)) {
            if (now - lastEmitTime >= timeout) {
                emit(value)
                lastEmitTime = now
            }
            lastValue = value
        } else {
            lastValue?.let { emit(it) }
            emit(value)
            lastValue = null
            lastEmitTime = now
        }
    }
}

// Использование
flow {
    emit(1)
    delay(100)
    emit(2)
    delay(200)
    emit(3)
}
.debounceIf(150) { it > 1 }
.collect { println(it) }

// Пользовательский оператор для ретраев
fun <T> Flow<T>.retryWithBackoff(
    retries: Int = 3,
    initialDelay: Long = 100,
    maxDelay: Long = 1000,
    factor: Double = 2.0
): Flow<T> = flow {
    var currentDelay = initialDelay
    repeat(retries) { attempt ->
        try {
            collect { emit(it) }
            return@flow
        } catch (e: Exception) {
            if (attempt == retries - 1) throw e
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }
}
```

Создание пользовательских операторов расширяет возможности **Flow** и позволяет адаптировать его под специфичные требования.

### Работа с несколькими Flow одновременно

**Координация нескольких **Flow** для обработки данных:**

```kotlin
// Объединение нескольких Flow
suspend fun combineMultipleFlows() {
    val flow1 = flowOf(1, 2, 3)
    val flow2 = flowOf("a", "b", "c")
    val flow3 = flowOf(true, false, true)

    combine(flow1, flow2, flow3) { num, str, bool ->
        Triple(num, str, bool)
    }.collect { (num, str, bool) ->
        println("$num: $str: $bool")
    }
}

// Выбор первого Flow с данными
suspend fun selectFirstFlow() {
    val flow1 = flow {
        delay(100)
        emit(1)
    }
    val flow2 = flow {
        delay(50)
        emit(2)
    }

    select<Int> {
        flow1.onEach { value ->
            emit(value)
        }.onEach {
            flow2.cancel()
        }

        flow2.onEach { value ->
            emit(value)
        }.onEach {
            flow1.cancel()
        }
    }.collect { value ->
        println(value)  // Получит значение из flow2 (быстрее)
    }
}
```

Работа с несколькими **Flow** позволяет создавать сложные реактивные цепочки и обрабатывать данные из различных источников.

## Производительность и оптимизация корутин

### Оптимизация использования памяти

**Оптимизация использования памяти при работе с корутинами:**

```kotlin
// Избегание захвата больших объектов
class LargeObjectProcessor {
    private val largeData = List(1_000_000) { it }  // Большой объект

    suspend fun process() {
        // Плохо - захватывает largeData
        launch {
            processData(largeData)  // Захватывает весь объект
        }

        // Хорошо - передаем только нужные данные
        val neededData = largeData.take(100)
        launch {
            processData(neededData)  // Захватывает только нужные данные
        }
    }
}

// Использование weak references для предотвращения утечек
class WeakReferenceProcessor {
    private val weakRef = WeakReference(largeObject)

    suspend fun process() {
        weakRef.get()?.let { obj ->
            processObject(obj)
        }
    }
}
```

Оптимизация использования памяти предотвращает утечки памяти и улучшает производительность приложения.

### Оптимизация производительности корутин

**Оптимизация производительности корутин для лучшей эффективности:**

```kotlin
// Использование правильного dispatcher
suspend fun optimizeDispatcher() = withContext(Dispatchers.Default) {
    // CPU-интенсивные операции
    computeHeavyTask()
}

// Batch обработка для улучшения производительности
suspend fun optimizeBatchProcessing(items: List<Item>) = coroutineScope {
    items.chunked(100).forEach { batch ->
        batch.forEach { item ->
            launch {
                processItem(item)
            }
        }
    }
}

// Использование ограничения параллелизма
suspend fun optimizeConcurrency(items: List<Item>) = coroutineScope {
    val semaphore = Semaphore(10)  // Максимум 10 параллельных операций

    items.map { item ->
        async {
            semaphore.withPermit {
                processItem(item)
            }
        }
    }.awaitAll()
}
```

Оптимизация производительности корутин критична для создания эффективных приложений, особенно при работе с большими объемами данных.

## Дополнительные техники Flow

### Работа с StateFlow и SharedFlow

**Использование **StateFlow** и **SharedFlow** для управления состоянием:**

```kotlin
import kotlinx.coroutines.flow.*

// StateFlow для состояния
class ViewModel {
    private val _state = MutableStateFlow(0)
    val state: StateFlow<Int> = _state.asStateFlow()

    fun increment() {
        _state.value++
    }
}

// SharedFlow для событий
class EventBus {
    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun emit(event: Event) {
        _events.emit(event)
    }
}

// Использование
val viewModel = ViewModel()
viewModel.state.collect { value ->
    println("State: $value")
}

val eventBus = EventBus()
eventBus.events.collect { event ->
    handleEvent(event)
}
```

**StateFlow** и **SharedFlow** позволяют эффективно управлять состоянием и событиями в приложении.

### Работа с Hot и Cold Flow

**Понимание различий между **Hot** и **Cold Flow**:**

```kotlin
// Cold Flow - данные генерируются при каждом collect
fun coldFlow(): Flow<Int> = flow {
    println("Flow started")
    repeat(5) {
        emit(it)
        delay(100)
    }
}

// Hot Flow - данные генерируются независимо от collect
fun hotFlow(): SharedFlow<Int> = MutableSharedFlow<Int>().also { flow ->
    GlobalScope.launch {
        repeat(5) {
            flow.emit(it)
            delay(100)
        }
    }
}

// Использование
val cold = coldFlow()
cold.collect { println("Collector 1: $it") }  // Flow started
cold.collect { println("Collector 2: $it") }  // Flow started (снова)

val hot = hotFlow()
delay(200)  // Даем время на генерацию данных
hot.collect { println("Collector 1: $it") }  // Может пропустить некоторые значения
```

Понимание различий между **Hot** и **Cold Flow** помогает выбирать правильный тип **Flow** для конкретных сценариев.

Этот файл содержит полное руководство по продвинутой конкурентности в **Kotlin**, покрывающее все основные аспекты от оптимизации производительности до интеграции с внешними библиотеками, продвинутых паттернов, мониторинга, отладки, работы с **Flow**, создания пользовательских операторов, координации нескольких **Flow**, оптимизации производительности, работы с **StateFlow** и **SharedFlow**, и понимания **Hot** и **Cold Flow**.

## Дополнительные техники конкурентности

### Работа с SupervisorJob

**Использование **SupervisorJob** для независимых корутин:**

```kotlin
// SupervisorJob для независимых корутин
fun main() = runBlocking {
    val supervisor = SupervisorJob()
    val scope = CoroutineScope(supervisor)

    scope.launch {
        delay(100)
        throw RuntimeException("Error in coroutine 1")
    }

    scope.launch {
        delay(200)
        println("Coroutine 2 completed")  // Выполнится несмотря на ошибку в первой
    }

    delay(300)
}

// SupervisorScope для автоматического управления
fun main() = runBlocking {
    supervisorScope {
        launch {
            delay(100)
            throw RuntimeException("Error")
        }

        launch {
            delay(200)
            println("This will execute")  // Выполнится
        }
    }
}
```

**SupervisorJob** позволяет независимым корутинам продолжать выполнение даже при ошибках в других корутинах.

### Работа с Channel и Flow вместе

**Интеграция **Channel** и **Flow** для сложных сценариев:**

```kotlin
// Преобразование Channel в Flow
fun <T> ReceiveChannel<T>.asFlow(): Flow<T> = flow {
    for (value in this@asFlow) {
        emit(value)
    }
}

// Преобразование Flow в Channel
suspend fun <T> Flow<T>.toChannel(capacity: Int = Channel.UNLIMITED): ReceiveChannel<T> {
    val channel = Channel<T>(capacity)

    launch {
        try {
            collect { value ->
                channel.send(value)
            }
        } finally {
            channel.close()
        }
    }

    return channel
}

// Использование
val channel = Channel<Int>()
val flow = channel.asFlow()

launch {
    repeat(10) {
        channel.send(it)
    }
    channel.close()
}

flow.collect { value ->
    println(value)
}
```

Интеграция **Channel** и **Flow** позволяет создавать гибкие системы обработки данных.

Этот файл содержит полное руководство по продвинутой конкурентности в **Kotlin**, покрывающее все основные аспекты от оптимизации производительности до интеграции с внешними библиотеками, продвинутых паттернов, мониторинга, отладки, работы с **Flow**, создания пользовательских операторов, координации нескольких **Flow**, оптимизации производительности, работы с **StateFlow** и **SharedFlow**, понимания **Hot** и **Cold Flow**, работы с **SupervisorJob** и интеграции **Channel** и **Flow**.

## Дополнительные техники

### Работа с корутинами и Actor Model

**Реализация **Actor Model** с корутинами:**

```kotlin
// Actor для изоляции состояния
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

// Использование
fun main() = runBlocking {
    val counter = counterActor()

    repeat(100) {
        counter.send(IncCounter)
    }

    val response = CompletableDeferred<Int>()
    counter.send(GetCounter(response))
    println("Counter: ${response.await()}")

    counter.close()
}
```

**Actor Model** позволяет изолировать состояние и обрабатывать сообщения последовательно, что упрощает работу с конкурентностью.

Этот файл содержит полное руководство по продвинутой конкурентности в **Kotlin**, покрывающее все основные аспекты от оптимизации производительности до интеграции с внешними библиотеками, продвинутых паттернов, мониторинга, отладки, работы с **Flow**, создания пользовательских операторов, координации нескольких **Flow**, оптимизации производительности, работы с **StateFlow** и **SharedFlow**, понимания **Hot** и **Cold Flow**, работы с **SupervisorJob**, интеграции **Channel** и **Flow** и **Actor Model**.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Продвинутые техники конкурентности в **Kotlin**, включая работу с **Flow**, **StateFlow**, **SharedFlow**, **SupervisorJob**, каналами и **Actor Model**, позволяют создавать сложные асинхронные системы с эффективным управлением состоянием и обработкой данных. Понимание этих концепций критично для создания масштабируемых и производительных приложений.

Этот файл содержит полное руководство по продвинутой конкурентности в **Kotlin**, покрывающее все основные аспекты от оптимизации производительности до интеграции с внешними библиотеками, продвинутых паттернов, мониторинга, отладки, работы с **Flow**, создания пользовательских операторов, координации нескольких **Flow**, оптимизации производительности, работы с **StateFlow** и **SharedFlow**, понимания **Hot** и **Cold Flow**, работы с **SupervisorJob**, интеграции **Channel** и **Flow**, **Actor Model** и заключение.

## Дополнительные ресурсы

**Для дальнейшего изучения продвинутой конкурентности в **Kotlin** рекомендуется:**

- **Kotlin Flow Advanced**: **https**://**kotlinlang.org**/**docs**/**flow.html**
- **StateFlow and SharedFlow**: **https**://**kotlinlang.org**/**docs**/**stateflow-`and-sharedflow`.html**
- **Coroutines Context and Dispatchers**: **https**://**kotlinlang.org**/**docs**/**coroutine-`context-and-dispatchers`.html**
- **Flow Operators**: **https**://**kotlinlang.org**/**api**/**kotlinx.coroutines**/**kotlinx-coroutines-core**/**kotlinx.coroutines.flow**/

Этот файл содержит полное руководство по продвинутой конкурентности в **Kotlin**, покрывающее все основные аспекты от оптимизации производительности до интеграции с внешними библиотеками, продвинутых паттернов, мониторинга, отладки, работы с **Flow**, создания пользовательских операторов, координации нескольких **Flow**, оптимизации производительности, работы с **StateFlow** и **SharedFlow**, понимания **Hot** и **Cold Flow**, работы с **SupervisorJob**, интеграции **Channel** и **Flow**, **Actor Model**, заключение и дополнительные ресурсы.

## Итоговые рекомендации

**При работе с продвинутой конкурентностью рекомендуется:**

1. Использовать **StateFlow** для управления состоянием `UI`
2. Использовать **SharedFlow** для событий и широковещательных сообщений
3. Правильно выбирать между **Hot** и **Cold Flow** для конкретных сценариев
4. Использовать **SupervisorJob** для независимых корутин
5. Интегрировать **Channel** и **Flow** для сложных сценариев

Этот файл содержит полное руководство по продвинутой конкурентности в **Kotlin**, покрывающее все основные аспекты от оптимизации производительности до интеграции с внешними библиотеками, продвинутых паттернов, мониторинга, отладки, работы с **Flow**, создания пользовательских операторов, координации нескольких **Flow**, оптимизации производительности, работы с **StateFlow** и **SharedFlow**, понимания **Hot** и **Cold Flow**, работы с **SupervisorJob**, интеграции **Channel** и **Flow**, **Actor Model**, заключение, дополнительные ресурсы и итоговые рекомендации.

## Практические примеры использования

### Создание пользовательских операторов Flow

**Создание пользовательских операторов для **Flow**:**

```kotlin
// Оператор для debounce с кастомной логикой
fun <T> Flow<T>.debounceWithCondition(
    condition: (T) -> Boolean,
    timeout: Long
): Flow<T> = flow {
    var lastEmitted: T? = null
    var lastEmitTime = 0L

    collect { value ->
        val currentTime = System.currentTimeMillis()
        if (condition(value)) {
            if (lastEmitted == null || currentTime - lastEmitTime >= timeout) {
                emit(value)
                lastEmitted = value
                lastEmitTime = currentTime
            }
        } else {
            if (lastEmitted != null) {
                emit(lastEmitted!!)
                lastEmitted = null
            }
        }
    }
}
```

Пользовательские операторы позволяют создавать специализированную логику для обработки потоков данных.

### Координация нескольких Flow

**Координация нескольких **Flow** для сложных сценариев:**

```kotlin
fun combineUserData(
    userId: Long
): Flow<UserData> = combine(
    getUserFlow(userId),
    getPostsFlow(userId),
    getCommentsFlow(userId)
) { user, posts, comments ->
    UserData(user, posts, comments)
}
```

Координация нескольких **Flow** позволяет комбинировать данные из различных источников.

### Использование SupervisorJob для независимых корутин

**Пример использования **SupervisorJob** для обработки независимых задач:**

```kotlin
class TaskProcessor {
    private val supervisorJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + supervisorJob)

    fun processTasks(tasks: List<Task>) {
        tasks.forEach { task ->
            scope.launch {
                try {
                    processTask(task)
                } catch (e: Exception) {
                    // Ошибка в одной задаче не отменяет другие
                    logError(task, e)
                }
            }
        }
    }

    private suspend fun processTask(task: Task) {
        // Обработка задачи
    }
}
```

**SupervisorJob** позволяет обрабатывать независимые задачи, где ошибка в одной не влияет на другие.

### Использование Mutex для синхронизации

**Пример использования **Mutex** для защиты критических секций:**

```kotlin
class Counter {
    private var value = 0
    private val mutex = Mutex()

    suspend fun increment() {
        mutex.withLock {
            value++
        }
    }

    suspend fun decrement() {
        mutex.withLock {
            value--
        }
    }

    suspend fun getValue(): Int {
        return mutex.withLock {
            value
        }
    }
}
```

**Mutex** обеспечивает взаимное исключение для корутин, предотвращая **race conditions**.

Этот файл содержит полное руководство по продвинутой конкурентности в **Kotlin**, покрывающее все основные аспекты от оптимизации производительности до интеграции с внешними библиотеками, продвинутых паттернов, мониторинга, отладки, работы с **Flow**, создания пользовательских операторов, координации нескольких **Flow**, оптимизации производительности, работы с **StateFlow** и **SharedFlow**, понимания **Hot** и **Cold Flow**, работы с **SupervisorJob**, интеграции **Channel** и **Flow**, **Actor Model**, практические примеры использования, включая **SupervisorJob** и **Mutex**, заключение, дополнительные ресурсы и итоговые рекомендации.

