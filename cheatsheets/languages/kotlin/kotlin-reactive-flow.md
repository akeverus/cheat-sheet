---
title: "Kotlin Reactive: Flow"
description: "Кратко: полное руководство по Kotlin Flow - нативной библиотеке для асинхронных потоков данных в Kotlin. Рассматриваются cold и hot flows, операторы, StateFlow, SharedFlow, интеграция с корутинами и лучшие практики."
tags:
  - languages
  - kotlin
  - kotlin-reactive-flow
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-04-20"
---
# Kotlin Reactive: Flow

Кратко: полное руководство по **Kotlin Flow** — нативной библиотеке для асинхронных потоков данных в **Kotlin**. Рассматриваются **cold** и **hot flows**, операторы, **StateFlow**, **SharedFlow**, интеграция с корутинами и лучшие практики.

## Полезные ссылки

### Официальная документация
- [Kotlin Flow](https://kotlinlang.org/docs/flow.html)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)

### Обучающие материалы
- [Kotlin Flow Tutorial](https://www.baeldung.com/kotlin/flow)

### См. также
- [[kotlin-basics|Основы Kotlin]]
- [[kotlin-concurrency-basics|Корутины]]
- [[kotlin-concurrency-advanced|Продвинутые темы корутин]]
- [[kotlin-reactive-rxkotlin|RxKotlin]]

- [[kotlin-performance|Kotlin Performance]]
## Содержание

- [Введение в Flow](#введение-в-flow)
  - [Основные концепции](#основные-концепции)
  - [Преимущества Flow](#преимущества-flow)
  - [Когда использовать Flow](#когда-использовать-flow)
- [Cold vs Hot Flows](#cold-vs-hot-flows)
  - [Cold Flow](#cold-flow)
  - [Hot Flow](#hot-flow)
- [Создание Flow](#создание-flow)
  - [flow builder](#flow-builder)
  - [flowOf](#flowof)
  - [asFlow](#asflow)
  - [channelFlow](#channelflow)
- [Операторы Flow](#операторы-flow)
  - [Операторы трансформации](#операторы-трансформации)
  - [Операторы фильтрации](#операторы-фильтрации)
  - [Операторы комбинирования](#операторы-комбинирования)
- [StateFlow](#stateflow)
  - [Создание StateFlow](#создание-stateflow)
  - [StateFlow vs LiveData](#stateflow-vs-livedata)
- [SharedFlow](#sharedflow)
  - [Создание SharedFlow](#создание-sharedflow)
  - [SharedFlow для событий](#sharedflow-для-событий)
- [Обработка ошибок](#обработка-ошибок)
  - [catch оператор](#catch-оператор)
  - [retry оператор](#retry-оператор)
  - [onCompletion](#oncompletion)
- [Backpressure](#backpressure)
  - [buffer](#buffer)
  - [conflate](#conflate)
  - [collectLatest](#collectlatest)
- [Интеграция с корутинами](#интеграция-с-корутинами)
- [Тестирование Flow](#тестирование-flow)
- [Лучшие практики](#лучшие-практики)
  - [Используйте StateFlow для состояния](#используйте-stateflow-для-состояния)
  - [Используйте SharedFlow для событий](#используйте-sharedflow-для-событий)
  - [Обрабатывайте ошибки](#обрабатывайте-ошибки)
  - [Управляйте backpressure](#управляйте-backpressure)
  - [Использование flowOn](#использование-flowon)
- [Продвинутые операторы Flow](#продвинутые-операторы-flow)
  - [Операторы для работы со временем](#операторы-для-работы-со-временем)
  - [Операторы для комбинирования Flow](#операторы-для-комбинирования-flow)
- [Работа с каналами](#работа-с-каналами)
  - [Преобразование Flow в Channel](#преобразование-flow-в-channel)
  - [Преобразование Channel в Flow](#преобразование-channel-в-flow)
- [Тестирование Flow](#тестирование-flow-1)
  - [Использование TestCoroutineScheduler](#использование-testcoroutinescheduler)
  - [Тестирование StateFlow и SharedFlow](#тестирование-stateflow-и-sharedflow)
- [Оптимизация производительности Flow](#оптимизация-производительности-flow)
  - [Избегайте создания лишних Flow](#избегайте-создания-лишних-flow)
  - [Использование shareIn для кэширования](#использование-sharein-для-кэширования)
- [Реальные примеры использования](#реальные-примеры-использования)
  - [Обработка сетевых запросов](#обработка-сетевых-запросов)
  - [Обработка UI событий](#обработка-ui-событий)
  - [Комбинирование нескольких источников данных](#комбинирование-нескольких-источников-данных)
- [Миграция с RxJava на Flow](#миграция-с-rxjava-на-flow)
  - [Сравнение операторов](#сравнение-операторов)
  - [Преимущества миграции](#преимущества-миграции)
- [Отладка Flow](#отладка-flow)
  - [Использование оператора onEach для логирования](#использование-оператора-oneach-для-логирования)
  - [Использование оператора catch для отладки ошибок](#использование-оператора-catch-для-отладки-ошибок)
- [Интеграция с Android](#интеграция-с-android)
  - [Использование Flow в Android](#использование-flow-в-android)
  - [Работа с ViewModel](#работа-с-viewmodel)
- [Продвинутые паттерны Flow](#продвинутые-паттерны-flow)
  - [Flow с retry логикой](#flow-с-retry-логикой)
  - [Flow с кэшированием](#flow-с-кэшированием)
- [Оптимизация производительности Flow](#оптимизация-производительности-flow-1)
  - [Оптимизация операторов Flow](#оптимизация-операторов-flow)
  - [Оптимизация памяти Flow](#оптимизация-памяти-flow)
- [Продвинутые техники Kotlin Flow](#продвинутые-техники-kotlin-flow)
  - [Создание кастомных операторов Flow](#создание-кастомных-операторов-flow)
  - [Работа с несколькими Flow одновременно](#работа-с-несколькими-flow-одновременно)
- [Производительность Flow в production](#производительность-flow-в-production)
  - [Мониторинг производительности Flow](#мониторинг-производительности-flow)
- [Дополнительные техники Flow](#дополнительные-техники-flow)
  - [Работа с SharedFlow и StateFlow](#работа-с-sharedflow-и-stateflow)
  - [Работа с Flow и корутинами](#работа-с-flow-и-корутинами)
- [Дополнительные техники Flow](#дополнительные-техники-flow-1)
  - [Работа с временными операторами Flow](#работа-с-временными-операторами-flow)
  - [Работа с операторами комбинирования](#работа-с-операторами-комбинирования)
- [Дополнительные техники Flow](#дополнительные-техники-flow-2)
  - [Работа с операторами обработки ошибок](#работа-с-операторами-обработки-ошибок)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Итоговые рекомендации](#итоговые-рекомендации)
- [Практические примеры использования](#практические-примеры-использования)
  - [Управление состоянием UI](#управление-состоянием-ui)
  - [Обработка событий](#обработка-событий)
  - [Использование Flow для обработки UI событий](#использование-flow-для-обработки-ui-событий)
  - [Использование Flow для загрузки данных](#использование-flow-для-загрузки-данных)

## Введение в Flow

**Kotlin Flow** — это библиотека для асинхронных потоков данных, встроенная в **Kotlin Coroutines**. **Flow** представляет последовательность значений, которые вычисляются асинхронно.

### Основные концепции

**Flow** построен на корутинах и следует тем же принципам. **Flow** является **cold** по умолчанию — он начинает испускать значения только при вызове терминальной операции (например, `collect`). Это отличает **Flow** от **RxJava Observable**, который может быть **hot**.

### Преимущества Flow

- **Нативная интеграция**: полностью интегрирован с корутинами **Kotlin**
- **Отмена**: автоматическая отмена при отмене корутины
- **Structured Concurrency**: следует принципам структурированной конкурентности
- **Простота**: более простой **API** по сравнению с **RxJava**
- **Производительность**: оптимизирован для **Kotlin**

### Когда использовать Flow

**Flow** идеально подходит для асинхронных потоков данных в **Kotlin** приложениях. Он заменяет **RxJava** для новых проектов и лучше интегрируется с экосистемой **Kotlin**.

## Cold vs Hot Flows

### Cold Flow

**Cold Flow** начинает испускать значения только при вызове терминальной операции. Каждая подписка создает новый поток данных:**

```kotlin
fun coldFlow(): Flow<Int> = flow {
    println("Flow started")
    repeat(3) {
        emit(it)
        delay(100)
    }
}

// Каждый collect создает новый поток
runBlocking {
    coldFlow().collect { println("Collector 1: $it") }
    coldFlow().collect { println("Collector 2: $it") }
}
// Вывод: Flow started, 0, 1, 2, Flow started, 0, 1, 2
```

**Cold Flow** подходит для источников данных, которые должны вычисляться заново для каждой подписки, например, результаты запросов к **API** или чтение файлов.

### Hot Flow

**Hot Flow** (StateFlow, SharedFlow) начинает испускать значения сразу после создания и делит один поток между всеми подписчиками:**

```kotlin
val stateFlow = MutableStateFlow(0)

runBlocking {
    stateFlow.collect { println("Collector 1: $it") }
    stateFlow.collect { println("Collector 2: $it") }
}
// Оба коллектора получают одни и те же значения
```

**Hot Flow** подходит для состояний приложения, событий `UI` и других данных, которые должны быть общими для всех подписчиков.

## Создание Flow

### flow builder

**Самый распространенный способ создания **Flow** — использование `flow` **builder**:**

```kotlin
fun numbersFlow(): Flow<Int> = flow {
    for (i in 1..5) {
        delay(100)
        emit(i)
    }
}
```

`flow` **builder** создает **cold Flow**. Функция `emit()` испускает значение в поток. **Builder** автоматически обрабатывает отмену корутины.

### flowOf

**Для создания **Flow** из фиксированных значений используется `flowOf`:**

```kotlin
val flow = flowOf(1, 2, 3, 4, 5)
```

`flowOf` создает **Flow**, который испускает указанные значения и завершается. Это аналог `Observable.just()` в **RxJava**.

### asFlow

**Коллекции можно преобразовать в **Flow** через `asFlow()`:**

```kotlin
val list = listOf(1, 2, 3, 4, 5)
val flow = list.asFlow()
```

`asFlow()` преобразует любую коллекцию в **Flow**. Это удобно для преобразования существующих данных в реактивный поток.

### channelFlow

**Для более сложных сценариев используется `channelFlow`:**

```kotlin
fun channelFlowExample(): Flow<Int> = channelFlow {
    launch {
        send(1)
        delay(100)
        send(2)
    }
    launch {
        delay(50)
        send(3)
    }
}
```

`channelFlow` позволяет испускать значения из разных корутин одновременно. Это полезно для объединения данных из нескольких источников.

## Операторы Flow

### Операторы трансформации

```kotlin
val flow = flowOf(1, 2, 3, 4, 5)

// map - преобразование каждого элемента
flow.map { it * 2 }
    .collect { println(it) }  // 2, 4, 6, 8, 10

// transform - кастомная трансформация
flow.transform { value ->
    emit(value)
    emit(value * 2)
}
.collect { println(it) }  // 1, 2, 2, 4, 3, 6, ...

// flatMapConcat - последовательное "разворачивание"
flow.flatMapConcat { value ->
    flowOf(value, value * 2)
}
.collect { println(it) }

// flatMapMerge - параллельное "разворачивание"
flow.flatMapMerge { value ->
    flowOf(value, value * 2)
}
.collect { println(it) }
```

`map` применяет функцию к каждому элементу. `transform` позволяет испускать произвольное количество элементов для каждого входного элемента. `flatMapConcat` обрабатывает вложенные **Flow** последовательно, `flatMapMerge` - параллельно.

### Операторы фильтрации

```kotlin
val flow = flowOf(1, 2, 3, 4, 5, 6)

// filter - фильтрация по условию
flow.filter { it % 2 == 0 }
    .collect { println(it) }  // 2, 4, 6

// take - взять первые N элементов
flow.take(3)
    .collect { println(it) }  // 1, 2, 3

// drop - пропустить первые N элементов
flow.drop(3)
    .collect { println(it) }  // 4, 5, 6

// distinctUntilChanged - пропуск повторяющихся значений
flowOf(1, 1, 2, 2, 3, 1)
    .distinctUntilChanged()
    .collect { println(it) }  // 1, 2, 3, 1
```

Операторы фильтрации позволяют выбирать нужные элементы из потока. Они не изменяют исходный **Flow**, а создают новый.

### Операторы комбинирования

```kotlin
val flow1 = flowOf(1, 2, 3)
val flow2 = flowOf("a", "b", "c")

// zip - объединение по позиции
flow1.zip(flow2) { num, letter ->
    "$num$letter"
}
.collect { println(it) }  // 1a, 2b, 3c

// combine - объединение последних значений
flow1.combine(flow2) { num, letter ->
    "$num$letter"
}
.collect { println(it) }
```

`zip` объединяет элементы из разных **Flow** по позиции. `combine` объединяет последние значения из каждого **Flow** при каждом новом значении.

## StateFlow

**StateFlow** — это **hot Flow**, который хранит текущее состояние и испускает его новым подписчикам.

### Создание StateFlow

```kotlin
val stateFlow = MutableStateFlow(0)

// Обновление значения
stateFlow.value = 1
stateFlow.update { it + 1 }

// Подписка
stateFlow.collect { value ->
    println("State: $value")
}
```

**StateFlow** всегда имеет значение, что делает его идеальным для хранения состояния приложения. Новые подписчики сразу получают текущее значение.

### StateFlow vs LiveData

**StateFlow** является заменой **LiveData** в **Android**:**

```kotlin
// Вместо LiveData
class ViewModel : ViewModel() {
    private val _state = MutableStateFlow(0)
    val state: StateFlow<Int> = _state.asStateFlow()

    fun increment() {
        _state.value++
    }
}
```

**StateFlow** предоставляет больше возможностей, чем **LiveData**: поддержку операторов, лучшую интеграцию с корутинами и типобезопасность.

## SharedFlow

**SharedFlow** — это **hot Flow** без начального значения, который может иметь несколько подписчиков.

### Создание SharedFlow

```kotlin
val sharedFlow = MutableSharedFlow<Int>(
    replay = 2,              // Количество последних значений для новых подписчиков
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
```

`replay` определяет, сколько последних значений получат новые подписчики. `extraBufferCapacity` добавляет дополнительный буфер для обработки временных перегрузок.

### SharedFlow для событий

**SharedFlow** идеально подходит для событий, которые должны быть обработаны один раз:**

```kotlin
class EventBus {
    private val _events = MutableSharedFlow<Event>(
        replay = 0,  // События не должны повторяться
        extraBufferCapacity = 64
    )
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun post(event: Event) {
        _events.emit(event)
    }
}
```

Использование `replay = 0` гарантирует, что события не будут повторяться для новых подписчиков, что важно для событийной архитектуры.

## Обработка ошибок

### catch оператор

**Для обработки ошибок в **Flow** используется оператор `catch`:**

```kotlin
flow {
    emit(1)
    throw RuntimeException("Error")
    emit(2)
}
.catch { e ->
    println("Caught: ${e.message}")
    emit(-1)  // Значение по умолчанию
}
.collect { println(it) }  // 1, -1
```

`catch` перехватывает ошибки из **upstream** и позволяет обработать их или испустить значения по умолчанию. Ошибки не распространяются дальше по цепочке.

### retry оператор

**Для повторных попыток используется `retry`:**

```kotlin
flow {
    emit(1)
    throw IOException("Network error")
}
.retry(3) { e ->
    e is IOException
}
.collect { println(it) }
```

`retry` повторяет **Flow** при ошибке. Условие определяет, при каких ошибках следует повторять попытку. Это полезно для сетевых запросов, где временные сбои могут быть исправлены повторной попыткой.

### onCompletion

**Для выполнения действий при завершении **Flow** используется `onCompletion`:**

```kotlin
flowOf(1, 2, 3)
    .onCompletion { cause ->
        if (cause == null) {
            println("Completed successfully")
        } else {
            println("Completed with error: ${cause.message}")
        }
    }
    .collect { println(it) }
```

`onCompletion` вызывается при нормальном завершении или при ошибке. Это позволяет выполнить **cleanup** или логирование.

## Backpressure

**Backpressure** возникает, когда производитель испускает значения быстрее, чем потребитель может их обработать.

### buffer

**Оператор `buffer` создает буфер для значений:**

```kotlin
flow {
    repeat(5) {
        emit(it)
        delay(100)
    }
}
.buffer(capacity = 3)
.collect { value ->
    delay(200)  // Медленная обработка
    println(value)
}
```

Буфер позволяет производителю продолжать испускать значения, даже если потребитель обрабатывает их медленно. Это повышает общую пропускную способность.

### conflate

**Оператор `conflate` сохраняет только последнее значение:**

```kotlin
flow {
    repeat(10) {
        emit(it)
        delay(50)
    }
}
.conflate()
.collect { value ->
    delay(200)
    println(value)  // Пропускает промежуточные значения
}
```

`conflate` полезен, когда важны только последние значения, а промежуточные можно пропустить, например, для обновлений `UI`.

### collectLatest

**`collectLatest` отменяет предыдущую обработку при поступлении нового значения:**

```kotlin
flow {
    repeat(10) {
        emit(it)
        delay(100)
    }
}
.collectLatest { value ->
    delay(200)  // Если новое значение придет, эта обработка отменится
    println(value)
}
```

Это полезно для операций, которые могут быть устаревшими при поступлении новых данных, например, обновление `UI` или выполнение запросов.

## Интеграция с корутинами

**Flow** полностью интегрирован с корутинами и может использоваться в любом контексте корутин:**

```kotlin
suspend fun processFlow() {
    flowOf(1, 2, 3)
        .map { it * 2 }
        .collect { println(it) }
}

// Использование в корутине
coroutineScope {
    launch {
        processFlow()
    }
}
```

**Flow** может использоваться в любых **suspend** функциях и корутинах, что делает его естественной частью асинхронного кода на **Kotlin**.

## Тестирование Flow

**Для тестирования **Flow** используется `runTest`:**

```kotlin
import kotlinx.coroutines.test.runTest

@Test
fun testFlow() = runTest {
    val flow = flowOf(1, 2, 3)
    val results = flow.toList()
    assertEquals(listOf(1, 2, 3), results)
}
```

`runTest` создает тестовое окружение для корутин, что позволяет тестировать **Flow** без реальных задержек.

## Лучшие практики

### Используйте StateFlow для состояния

**StateFlow** идеально подходит для хранения состояния приложения, так как всегда имеет значение и автоматически уведомляет подписчиков об изменениях.

### Используйте SharedFlow для событий

**SharedFlow** с `replay = 0` подходит для событий, которые должны быть обработаны один раз и не должны повторяться для новых подписчиков.

### Обрабатывайте ошибки

Всегда используйте `catch` для обработки ошибок в **Flow**, чтобы предотвратить неожиданные сбои приложения.

### Управляйте backpressure

Используйте `buffer`, `conflate` или `collectLatest` в зависимости от требований к обработке данных для управления **backpressure**.

### Использование flowOn

**`flowOn` позволяет изменить контекст выполнения для операций выше по цепочке:**

```kotlin
flow {
    // Выполняется в Dispatchers.Default
    repeat(10) {
        emit(computeValue(it))
    }
}
.flowOn(Dispatchers.Default)  // Меняет контекст для операций выше
.map { it.toString() }  // Выполняется в контексте collect
.collect { println(it) }  // Выполняется в контексте collect
```

`flowOn` создает буфер между операциями, что может влиять на производительность. Используйте его осознанно, особенно для операций, которые должны выполняться в определенном контексте.

## Продвинутые операторы Flow

### Операторы для работы со временем

**Flow** предоставляет операторы для работы с временем и задержками:**

```kotlin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.delay

// debounce - пропуск элементов, если следующий приходит слишком быстро
flow {
    repeat(10) {
        emit(it)
        delay(50)
    }
}
.debounce(200)  // Пропустит элементы, приходящие быстрее 200ms
.collect { println(it) }

// sample - выборка элементов с интервалом
flow {
    repeat(100) {
        emit(it)
        delay(10)
    }
}
.sample(100)  // Берет элемент каждые 100ms
.collect { println(it) }

// timeout - таймаут для операций
flow {
    delay(2000)
    emit(1)
}
.timeout(1000)  // Выбросит TimeoutCancellationException
.collect { println(it) }
```

Операторы времени критичны для работы с `UI` событиями, где важно контролировать частоту обновлений и предотвращать избыточные операции.

### Операторы для комбинирования Flow

```kotlin
// combine - комбинирование последних значений
val flow1 = flowOf(1, 2, 3)
val flow2 = flowOf("a", "b", "c")

flow1.combine(flow2) { num, letter ->
    "$num$letter"
}.collect { println(it) }  // 1a, 2b, 3c

// zip - объединение по позиции
flow1.zip(flow2) { num, letter ->
    "$num$letter"
}.collect { println(it) }  // 1a, 2b, 3c

// merge - объединение потоков
merge(flow1, flow2).collect { println(it) }  // 1, a, 2, b, 3, c

// flatMapLatest - переключение на новый Flow при новом значении
flow1.flatMapLatest { value ->
    flow {
        delay(100)
        emit(value * 2)
    }
}.collect { println(it) }  // Только последнее значение
```

Комбинирование **Flow** позволяет создавать сложные реактивные цепочки, которые автоматически обновляются при изменении любого источника.

## Работа с каналами

**Flow** может быть преобразован в **Channel** и наоборот.

### Преобразование Flow в Channel

```kotlin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach

val flow = flowOf(1, 2, 3, 4, 5)
val channel = flow.produceIn(this)  // Создает BroadcastChannel

channel.consumeEach { value ->
    println(value)
}
```

Преобразование **Flow** в **Channel** полезно, когда нужно несколько подписчиков или интеграция с кодом, использующим каналы.

### Преобразование Channel в Flow

```kotlin
val channel = Channel<Int>()

launch {
    for (i in 1..5) {
        channel.send(i)
    }
    channel.close()
}

channel.receiveAsFlow()
    .collect { value ->
        println(value)
    }
```

Преобразование **Channel** в **Flow** позволяет использовать операторы **Flow** с данными из каналов.

## Тестирование Flow

Продвинутое тестирование **Flow** с использованием различных инструментов.

### Использование TestCoroutineScheduler

```kotlin
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.StandardTestDispatcher

@Test
fun testFlowWithTime() = runTest {
    val scheduler = TestCoroutineScheduler()
    val dispatcher = StandardTestDispatcher(scheduler)

    val flow = flow {
        emit(1)
        delay(1000)
        emit(2)
    }.flowOn(dispatcher)

    val results = mutableListOf<Int>()
    val job = launch(dispatcher) {
        flow.collect { results.add(it) }
    }

    scheduler.advanceTimeBy(500)
    assertEquals(listOf(1), results)

    scheduler.advanceTimeBy(500)
    assertEquals(listOf(1, 2), results)

    job.cancel()
}
```

**TestCoroutineScheduler** позволяет управлять временем в тестах, что делает тестирование операторов времени быстрым и предсказуемым.

### Тестирование StateFlow и SharedFlow

```kotlin
@Test
fun testStateFlow() = runTest {
    val stateFlow = MutableStateFlow(0)

    val results = mutableListOf<Int>()
    val job = launch {
        stateFlow.collect { results.add(it) }
    }

    stateFlow.value = 1
    stateFlow.value = 2

    assertEquals(listOf(0, 1, 2), results)

    job.cancel()
}
```

Тестирование **StateFlow** и **SharedFlow** требует понимания их поведения: **StateFlow** всегда имеет значение и эмитит его сразу при подписке.

## Оптимизация производительности Flow

### Избегайте создания лишних Flow

```kotlin
// Плохо - создает новый Flow на каждой итерации
fun processItems(items: List<Int>): Flow<Int> {
    return items.asFlow()
        .flatMapConcat { item ->
            flowOf(item * 2)  // Создает новый Flow
        }
}

// Хорошо - использует map
fun processItems(items: List<Int>): Flow<Int> {
    return items.asFlow()
        .map { it * 2 }  // Не создает новый Flow
}
```

Использование правильных операторов уменьшает создание промежуточных объектов и улучшает производительность.

### Использование shareIn для кэширования

```kotlin
val sharedFlow = flow {
    emit(expensiveOperation())
}.shareIn(
    scope = CoroutineScope(Dispatchers.Default),
    started = SharingStarted.WhileSubscribed(5000),
    replay = 1
)

// Первая подписка выполнит операцию
sharedFlow.collect { println(it) }

// Последующие подписки получат кэшированное значение
sharedFlow.collect { println(it) }
```

`shareIn` позволяет кэшировать результаты **Flow** и переиспользовать их между подписчиками, что особенно полезно для дорогих операций.

## Реальные примеры использования

### Обработка сетевых запросов

```kotlin
fun fetchUserData(userId: Int): Flow<User> = flow {
    val user = apiService.getUser(userId)
    emit(user)
}
.flowOn(Dispatchers.IO)
.retry(3) { error ->
    error is IOException
}
.catch { error ->
    emit(User.default())  // Возврат значения по умолчанию
}
```

Обработка сетевых запросов с **retry** логикой и обработкой ошибок — типичный случай использования **Flow**.

### Обработка `UI` событий

```kotlin
searchEditText.textChanges()
    .debounce(300)
    .filter { it.length > 2 }
    .distinctUntilChanged()
    .flatMapLatest { query ->
        searchService.search(query)
            .catch { emit(emptyList()) }
    }
    .flowOn(Dispatchers.IO)
    .collect { results ->
        updateSearchResults(results)
    }
```

Обработка `UI` событий с **debounce** и **flatMapLatest** предотвращает избыточные запросы и обеспечивает плавную работу интерфейса.

### Комбинирование нескольких источников данных

```kotlin
combine(
    userFlow,
    settingsFlow,
    notificationsFlow
) { user, settings, notifications ->
    DashboardData(user, settings, notifications)
}.collect { dashboardData ->
    updateDashboard(dashboardData)
}
```

Комбинирование нескольких источников данных позволяет создавать сложные реактивные цепочки, которые автоматически обновляются при изменении любого источника.

## Миграция с RxJava на Flow

Для проектов, мигрирующих с **RxJava** на **Flow**, важно понимать различия.

### Сравнение операторов

```kotlin
// RxJava
Observable.range(1, 10)
    .map { it * 2 }
    .filter { it > 5 }
    .subscribe { println(it) }

// Kotlin Flow
flow {
    for (i in 1..10) emit(i)
}
.map { it * 2 }
.filter { it > 5 }
.collect { println(it) }
```

Большинство операторов имеют прямые аналоги, что упрощает миграцию.

### Преимущества миграции

- Нативная интеграция с корутинами
- **Structured concurrency**
- Более простой **API**
- Лучшая производительность для **Kotlin** кода
- Отсутствие зависимости от **RxJava**

Однако миграция требует времени и тестирования, особенно для сложных реактивных цепочек.

## Отладка Flow

Отладка **Flow** может быть сложной из-за асинхронной природы.

### Использование оператора onEach для логирования

```kotlin
flow {
    emit(1)
    emit(2)
    emit(3)
}
.onEach { value ->
    println("Emitted: $value")
}
.collect { value ->
    println("Collected: $value")
}
```

`onEach` позволяет логировать значения на любом этапе цепочки, что помогает понять поток данных.

### Использование оператора catch для отладки ошибок

```kotlin
flow {
    emit(1)
    throw RuntimeException("Error")
    emit(2)
}
.catch { error ->
    println("Caught error: ${error.message}")
    emit(-1)  // Значение по умолчанию
}
.collect { value ->
    println(value)
}
```

Правильная обработка ошибок с логированием помогает выявить проблемы в реактивных цепочках.

Этот файл содержит полное руководство по **Kotlin Flow**, покрывающее все основные аспекты работы с асинхронными потоками данных в **Kotlin**, включая продвинутые операторы, тестирование, оптимизацию, реальные примеры использования и миграцию с **RxJava**.

## Интеграция с Android

### Использование Flow в Android

**Flow** отлично интегрируется с **Android** для обработки асинхронных данных:**

```kotlin
class MainActivity : AppCompatActivity() {
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Запуск Flow в lifecycleScope
        job = lifecycleScope.launch {
            searchFlow()
                .debounce(300)
                .collect { query ->
                    performSearch(query)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()  // Отмена при уничтожении Activity
    }
}
```

Использование **lifecycleScope** обеспечивает автоматическую отмену **Flow** при уничтожении компонента, что предотвращает утечки памяти.

### Работа с ViewModel

**Интеграция **Flow** с **ViewModel**:**

```kotlin
class MyViewModel : ViewModel() {
    private val _data = MutableStateFlow<List<Item>>(emptyList())
    val data: StateFlow<List<Item>> = _data.asStateFlow()

    init {
        viewModelScope.launch {
            loadData()
                .collect { items ->
                    _data.value = items
                }
        }
    }

    private fun loadData(): Flow<List<Item>> = flow {
        emit(dataRepository.getAll())
    }
}

// Использование в Activity/Fragment
lifecycleScope.launch {
    viewModel.data.collect { items ->
        updateUI(items)
    }
}
```

**StateFlow** в **ViewModel** обеспечивает реактивное обновление `UI` при изменении данных.

## Продвинутые паттерны Flow

### Flow с retry логикой

**Реализация **Flow** с умной **retry** логикой:**

```kotlin
fun <T> Flow<T>.retryWithBackoff(
    retries: Int = 3,
    initialDelay: Long = 100,
    maxDelay: Long = 1000,
    factor: Double = 2.0,
    onRetry: (Throwable, Long) -> Unit = { _, _ -> }
): Flow<T> = flow {
    var currentDelay = initialDelay
    repeat(retries) { attempt ->
        try {
            collect { emit(it) }
            return@flow
        } catch (e: Exception) {
            if (attempt == retries - 1) throw e
            onRetry(e, currentDelay)
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }
}

// Использование
networkFlow
    .retryWithBackoff(
        retries = 5,
        initialDelay = 100L,
        maxDelay = 5000L
    ) { error, delay ->
        println("Retrying after $delay ms: ${error.message}")
    }
    .collect { data ->
        processData(data)
    }
```

**Retry** с экспоненциальной задержкой позволяет эффективно обрабатывать временные ошибки сети.

### Flow с кэшированием

**Реализация **Flow** с кэшированием результатов:**

```kotlin
class CachedFlow<T>(
    private val source: Flow<T>,
    private val cache: MutableSharedFlow<T> = MutableSharedFlow(replay = 1)
) : Flow<T> by cache {

    fun start() {
        CoroutineScope(Dispatchers.Default).launch {
            source.collect { value ->
                cache.emit(value)
            }
        }
    }
}

// Использование
val cachedFlow = CachedFlow(dataFlow)
cachedFlow.start()

// Первая подписка получит последнее кэшированное значение
cachedFlow.collect { println(it) }

// Последующие подписки также получат кэшированное значение
cachedFlow.collect { println(it) }
```

Кэширование **Flow** позволяет переиспользовать результаты между подписчиками и улучшает производительность.

## Оптимизация производительности Flow

### Оптимизация операторов Flow

**Правильное использование операторов для оптимизации производительности:**

```kotlin
// Используйте buffer для производительности
flow {
    repeat(100) {
        emit(it)
    }
}
.buffer(capacity = 10)  // Буферизация элементов
.collect { value ->
    processValue(value)  // Обработка может быть медленнее генерации
}

// Используйте conflate для пропуска промежуточных значений
flow {
    repeat(100) {
        emit(it)
        delay(10)
    }
}
.conflate()  // Пропускает промежуточные значения
.collect { value ->
    delay(100)  // Медленная обработка
    println(value)  // Получит только последние значения
}

// Используйте collectLatest для отмены предыдущей обработки
flow {
    repeat(100) {
        emit(it)
        delay(10)
    }
}
.collectLatest { value ->
    delay(100)  // Если новое значение придет, обработка отменится
    println(value)  // Обработает только последние значения
}
```

Правильный выбор операторов для управления потоком данных улучшает производительность и предотвращает переполнение буферов.

### Оптимизация памяти Flow

**Оптимизация использования памяти при работе с **Flow**:**

```kotlin
// Используйте take для ограничения количества элементов
largeFlow
    .take(1000)  // Берет только первые 1000 элементов
    .collect { process(it) }

// Используйте distinctUntilChanged для предотвращения дубликатов
flow {
    repeat(100) {
        emit(it / 10)  // Много дубликатов
    }
}
.distinctUntilChanged()  // Пропускает повторяющиеся значения
.collect { println(it) }

// Очищайте ресурсы в finally блоке
try {
    flow.collect { value ->
        processValue(value)
    }
} finally {
    // Очистка ресурсов
    cleanup()
}
```

Оптимизация памяти предотвращает утечки памяти и улучшает производительность приложения.

## Продвинутые техники Kotlin Flow

### Создание кастомных операторов Flow

**Создание пользовательских операторов для **Flow**:**

```kotlin
// Кастомный оператор для batch обработки
fun <T> Flow<T>.batch(size: Int): Flow<List<T>> = flow {
    val buffer = mutableListOf<T>()

    collect { value ->
        buffer.add(value)
        if (buffer.size >= size) {
            emit(buffer.toList())
            buffer.clear()
        }
    }

    if (buffer.isNotEmpty()) {
        emit(buffer)
    }
}

// Использование
flow {
    repeat(100) { emit(it) }
}
.batch(10)
.collect { batch ->
    println("Batch: $batch")
}

// Кастомный оператор для window операций
fun <T> Flow<T>.windowed(size: Int, step: Int = 1): Flow<List<T>> = flow {
    val window = mutableListOf<T>()

    collect { value ->
        window.add(value)
        if (window.size >= size) {
            emit(window.toList())
            repeat(step) {
                if (window.isNotEmpty()) {
                    window.removeAt(0)
                }
            }
        }
    }
}

// Использование
flow {
    repeat(20) { emit(it) }
}
.windowed(5, step = 2)
.collect { window ->
    println("Window: $window")
}

// Кастомный оператор для rate limiting
fun <T> Flow<T>.rateLimit(permits: Int, period: Long, unit: TimeUnit = TimeUnit.SECONDS): Flow<T> = flow {
    val interval = unit.toMillis(period)
    var lastEmitTime = 0L
    var permitCount = 0

    collect { value ->
        val now = System.currentTimeMillis()

        if (now - lastEmitTime >= interval) {
            permitCount = 0
            lastEmitTime = now
        }

        if (permitCount < permits) {
            emit(value)
            permitCount++
        }
    }
}
```

Кастомные операторы **Flow** позволяют создавать переиспользуемую функциональность для специфичных сценариев.

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

// Слияние нескольких Flow
suspend fun mergeMultipleFlows() {
    val flow1 = flowOf(1, 2, 3)
    val flow2 = flowOf(4, 5, 6)

    merge(flow1, flow2).collect { value ->
        println(value)
    }
}

// Zip для синхронизации Flow
suspend fun zipFlows() {
    val flow1 = flowOf(1, 2, 3)
    val flow2 = flowOf("a", "b", "c")

    flow1.zip(flow2) { num, str ->
        "$num: $str"
    }.collect { result ->
        println(result)
    }
}

// SwitchMap для переключения между Flow
fun searchFlow(query: String): Flow<List<String>> {
    return flow {
        emit(searchService.search(query))
    }
}

suspend fun handleSearchQueries() {
    queryFlow
        .flatMapLatest { query ->
            searchFlow(query)
        }
        .collect { results ->
            updateResults(results)
        }
}
```

Работа с несколькими **Flow** позволяет создавать сложные реактивные цепочки и обрабатывать данные из различных источников.

## Производительность Flow в production

### Мониторинг производительности Flow

**Мониторинг производительности **Flow** приложений:**

```kotlin
// Метрики для Flow
class FlowMetrics {
    private val requestCount = AtomicLong(0)
    private val errorCount = AtomicLong(0)
    private val averageLatency = AtomicReference<Double>(0.0)

    fun <T> Flow<T>.withMetrics(): Flow<T> = flow {
        requestCount.incrementAndGet()
        val startTime = System.nanoTime()

        try {
            collect { value ->
                emit(value)
            }
            recordSuccess(System.nanoTime() - startTime)
        } catch (e: Exception) {
            errorCount.incrementAndGet()
            throw e
        }
    }

    private fun recordSuccess(duration: Long) {
        val currentAvg = averageLatency.get()
        val newAvg = (currentAvg + duration / 1_000_000.0) / 2
        averageLatency.set(newAvg)
    }

    fun getMetrics(): Map<String, Any> {
        return mapOf(
            "requests" to requestCount.get(),
            "errors" to errorCount.get(),
            "errorRate" to (errorCount.get().toDouble() / requestCount.get()),
            "averageLatency" to averageLatency.get()
        )
    }
}

// Использование
val metrics = FlowMetrics()
dataFlow
    .withMetrics()
    .collect { data ->
        processData(data)
    }
```

Мониторинг производительности **Flow** позволяет отслеживать состояние потоков данных и выявлять проблемы в **production**.

## Дополнительные техники Flow

### Работа с SharedFlow и StateFlow

**Продвинутое использование **SharedFlow** и **StateFlow**:**

```kotlin
import kotlinx.coroutines.flow.*

// SharedFlow с replay
val sharedFlow = MutableSharedFlow<Int>(
    replay = 2,  // Хранит последние 2 значения
    extraBufferCapacity = 10  // Дополнительный буфер
)

// Подписчики получат последние 2 значения
sharedFlow.emit(1)
sharedFlow.emit(2)
sharedFlow.emit(3)

sharedFlow.collect { value ->
    println("Collector: $value")  // Получит 2 и 3
}

// StateFlow для состояния
class Counter {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun increment() {
        _count.value++
    }

    fun reset() {
        _count.value = 0
    }
}

// Использование
val counter = Counter()
counter.count.collect { value ->
    println("Count: $value")
}

counter.increment()  // Count: 1
counter.increment()  // Count: 2
counter.reset()      // Count: 0
```

**SharedFlow** и **StateFlow** позволяют эффективно управлять состоянием и событиями в приложении.

### Работа с Flow и корутинами

**Интеграция **Flow** с корутинами:**

```kotlin
// Flow в корутинах
suspend fun processFlow() = coroutineScope {
    val flow = flow {
        repeat(10) {
            emit(it)
            delay(100)
        }
    }

    flow
        .onEach { println("Emitted: $it") }
        .collect { value ->
            println("Collected: $value")
        }
}

// Параллельная обработка Flow
suspend fun processFlowParallel() = coroutineScope {
    val flow = flow {
        repeat(100) {
            emit(it)
        }
    }

    flow
        .buffer()  // Буферизация для параллельной обработки
        .map { value ->
            async {
                processValue(value)
            }
        }
        .collect { deferred ->
            val result = deferred.await()
            println("Result: $result")
        }
}

// Обработка ошибок в Flow
suspend fun handleFlowErrors() {
    flow {
        repeat(10) {
            if (it == 5) throw RuntimeException("Error at $it")
            emit(it)
        }
    }
    .catch { error ->
        println("Caught error: ${error.message}")
        emit(-1)  // Значение по умолчанию
    }
    .collect { value ->
        println(value)
    }
}
```

Интеграция **Flow** с корутинами позволяет создавать эффективные асинхронные потоки данных.

Этот файл содержит полное руководство по **Kotlin Flow**, покрывающее все основные аспекты работы с асинхронными потоками данных в **Kotlin**, включая продвинутые операторы, тестирование, оптимизацию, реальные примеры использования, миграцию с **RxJava**, интеграцию с **Android**, продвинутые паттерны, оптимизацию производительности, мониторинг, работу с **SharedFlow** и **StateFlow**, и интеграцию с корутинами.

## Дополнительные техники Flow

### Работа с временными операторами Flow

**Использование временных операторов в **Flow**:**

```kotlin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.delay

// Задержка эмиссии
flow {
    repeat(10) {
        emit(it)
    }
}
.delayEach(100)  // Задержка между эмиссиями
.collect { println(it) }

// Таймаут
flow {
    repeat(10) {
        delay(50)
        emit(it)
    }
}
.timeout(200)  // Таймаут на всю операцию
.collect { println(it) }

// Debounce
searchFlow
    .debounce(300)
    .collect { performSearch(it) }

// Sample - берет последнее значение за период
flow {
    repeat(100) {
        emit(it)
        delay(10)
    }
}
.sample(100)  // Берет значение каждые 100ms
.collect { println(it) }
```

Временные операторы **Flow** позволяют контролировать время эмиссии и обработки данных.

### Работа с операторами комбинирования

**Комбинирование нескольких **Flow**:**

```kotlin
// Combine - комбинирует последние значения
val flow1 = flowOf(1, 2, 3)
val flow2 = flowOf("a", "b", "c")

combine(flow1, flow2) { num, str ->
    "$num: $str"
}.collect { println(it) }

// Zip - комбинирует по порядку
flow1.zip(flow2) { num, str ->
    "$num: $str"
}.collect { println(it) }

// Merge - объединяет потоки
merge(flow1, flow2.map { it.hashCode() })
    .collect { println(it) }

// FlatMap - преобразует каждый элемент в Flow
flow1.flatMapConcat { value ->
    flow {
        repeat(3) {
            emit(value * it)
        }
    }
}.collect { println(it) }
```

Операторы комбинирования позволяют создавать сложные реактивные цепочки из нескольких источников данных.

Этот файл содержит полное руководство по **Kotlin Flow**, покрывающее все основные аспекты работы с асинхронными потоками данных в **Kotlin**, включая продвинутые операторы, тестирование, оптимизацию, реальные примеры использования, миграцию с **RxJava**, интеграцию с **Android**, продвинутые паттерны, оптимизацию производительности, мониторинг, работу с **SharedFlow** и **StateFlow**, интеграцию с корутинами, временными операторами и операторами комбинирования.

## Дополнительные техники Flow

### Работа с операторами обработки ошибок

**Использование операторов обработки ошибок:**

```kotlin
import kotlinx.coroutines.flow.*

// Обработка ошибок
flow {
    repeat(10) {
        if (it == 5) throw RuntimeException("Error at $it")
        emit(it)
    }
}
.catch { error ->
    println("Caught error: ${error.message}")
    emit(-1)  // Значение по умолчанию
}
.collect { value ->
    println(value)
}

// Retry при ошибках
flow {
    repeat(10) {
        if (it == 5) throw RuntimeException("Error")
        emit(it)
    }
}
.retry(3) { error ->
    error is RuntimeException
}
.collect { println(it) }

// Retry с экспоненциальной задержкой
flow { /* ... */ }
.retry(3) { error, attempt ->
    delay(100L * attempt)
    error is RuntimeException
}
.collect { println(it) }
```

Операторы обработки ошибок позволяют **gracefully** обрабатывать ошибки в реактивных потоках.

Этот файл содержит полное руководство по **Kotlin Flow**, покрывающее все основные аспекты работы с асинхронными потоками данных в **Kotlin**, включая продвинутые операторы, тестирование, оптимизацию, реальные примеры использования, миграцию с **RxJava**, интеграцию с **Android**, продвинутые паттерны, оптимизацию производительности, мониторинг, работу с **SharedFlow** и **StateFlow**, интеграцию с корутинами, временными операторами, операторами комбинирования и обработки ошибок.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

**Kotlin Flow** предоставляет нативный способ работы с асинхронными потоками данных в **Kotlin**. Понимание основных концепций **Flow**, включая **StateFlow**, **SharedFlow**, операторы комбинирования, временные операторы и обработку ошибок, позволяет создавать эффективные реактивные приложения. Правильное использование **Flow** помогает обрабатывать потоки данных, управлять состоянием и создавать отзывчивые пользовательские интерфейсы.

Этот файл содержит полное руководство по **Kotlin Flow**, покрывающее все основные аспекты работы с асинхронными потоками данных в **Kotlin**, включая продвинутые операторы, тестирование, оптимизацию, реальные примеры использования, миграцию с **RxJava**, интеграцию с **Android**, продвинутые паттерны, оптимизацию производительности, мониторинг, работу с **SharedFlow** и **StateFlow**, интеграцию с корутинами, временными операторами, операторами комбинирования, обработки ошибок и заключение.

## Дополнительные ресурсы

**Для дальнейшего изучения **Kotlin Flow** рекомендуется:**

- **Kotlin Flow Documentation**: **https**://**kotlinlang.org**/**docs**/**flow.html**
- **StateFlow and SharedFlow**: **https**://**kotlinlang.org**/**docs**/**stateflow-`and-sharedflow`.html**
- **Flow Operators**: **https**://**kotlinlang.org**/**api**/**kotlinx.coroutines**/**kotlinx-coroutines-core**/**kotlinx.coroutines.flow**/

Этот файл содержит полное руководство по **Kotlin Flow**, покрывающее все основные аспекты работы с асинхронными потоками данных в **Kotlin**, включая продвинутые операторы, тестирование, оптимизацию, реальные примеры использования, миграцию с **RxJava**, интеграцию с **Android**, продвинутые паттерны, оптимизацию производительности, мониторинг, работу с **SharedFlow** и **StateFlow**, интеграцию с корутинами, временными операторами, операторами комбинирования, обработки ошибок, заключение и дополнительные ресурсы.

## Итоговые рекомендации

**При работе с **Kotlin Flow** важно:**

1. Использовать **StateFlow** для управления состоянием `UI`
2. Использовать **SharedFlow** для событий и широковещательных сообщений
3. Правильно обрабатывать ошибки с помощью операторов **catch** и **retry**
4. Использовать **flowOn** для управления контекстом выполнения
5. Тестировать **Flow** с использованием **runTest** и **Turbine**

Этот файл содержит полное руководство по **Kotlin Flow**, покрывающее все основные аспекты работы с асинхронными потоками данных в **Kotlin**, включая продвинутые операторы, тестирование, оптимизацию, реальные примеры использования, миграцию с **RxJava**, интеграцию с **Android**, продвинутые паттерны, оптимизацию производительности, мониторинг, работу с **SharedFlow** и **StateFlow**, интеграцию с корутинами, временными операторами, операторами комбинирования, обработки ошибок, заключение, дополнительные ресурсы и итоговые рекомендации.

## Практические примеры использования

### Управление состоянием `UI`

**Использование **StateFlow** для управления состоянием `UI`:**

```kotlin
class UserViewModel {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadUser(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            try {
                val user = userRepository.getUser(userId)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    user = user
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message
                )
            }
        }
    }
}
```

**StateFlow** позволяет эффективно управлять состоянием `UI` в реактивном стиле.

### Обработка событий

**Использование **SharedFlow** для обработки событий:**

```kotlin
class EventBus {
    private val _events = MutableSharedFlow<Event>(replay = 0)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun emit(event: Event) {
        _events.emit(event)
    }
}

// Использование
val eventBus = EventBus()
eventBus.events.collect { event ->
    when (event) {
        is UserLoggedIn -> handleLogin(event)
        is UserLoggedOut -> handleLogout(event)
    }
}
```

**SharedFlow** позволяет эффективно обрабатывать события в приложении.

### Использование Flow для обработки `UI` событий

**Пример использования **Flow** для обработки событий `UI`:**

```kotlin
class SearchViewModel {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<SearchResult>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(emptyList())
            } else {
                searchService.search(query)
                    .catch { emit(emptyList()) }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateQuery(query: String) {
        _searchQuery.value = query
    }
}
```

**Flow** позволяет эффективно обрабатывать пользовательский ввод с **debounce** и отменой предыдущих запросов.

### Использование Flow для загрузки данных

**Пример использования **Flow** для загрузки данных с кэшированием:**

```kotlin
class DataRepository {
    private val cache = mutableMapOf<Long, User>()

    fun getUserFlow(userId: Long): Flow<User> = flow {
        // Проверка кэша
        cache[userId]?.let {
            emit(it)
            return@flow
        }

        // Загрузка из сети
        val user = apiService.getUser(userId)
        cache[userId] = user
        emit(user)
    }
    .flowOn(Dispatchers.IO)
    .shareIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5000),
        replay = 1
    )
}
```

**Flow** с кэшированием позволяет эффективно загружать и кэшировать данные.

Этот файл содержит полное руководство по **Kotlin Flow**, покрывающее все основные аспекты работы с асинхронными потоками данных в **Kotlin**, включая продвинутые операторы, тестирование, оптимизацию, реальные примеры использования, миграцию с **RxJava**, интеграцию с **Android**, продвинутые паттерны, оптимизацию производительности, мониторинг, работу с **SharedFlow** и **StateFlow**, интеграцию с корутинами, временными операторами, операторами комбинирования, обработки ошибок, практические примеры использования, включая обработку `UI` событий и загрузку данных, заключение, дополнительные ресурсы и итоговые рекомендации.

