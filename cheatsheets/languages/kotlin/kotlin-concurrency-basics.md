---
title: "Kotlin Concurrency Basics"
description: "Полное руководство по конкурентности в Kotlin: корутины, Flow, каналы, диспетчеры, паттерны асинхронного программирования"
tags:
  - kotlin
  - concurrency
  - coroutines
  - flow
  - channels
  - async
  - reactive
difficulty: "intermediate"
prerequisites: ["kotlin-basics.md"]
next: ["kotlin-advanced.md"]
updated: "2026-04-20"
---

# Kotlin Concurrency Basics

**Конкурентность в Kotlin** — это мощная система для написания асинхронного и параллельного кода. Основанная на корутинах (coroutines), она позволяет писать асинхронный код в синхронном стиле, обеспечивая высокую производительность и безопасность.

## Полезные ссылки

### Официальная документация

- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Kotlin API Reference](https://kotlinlang.org/api/latest/jvm/stdlib/)

### Обучающие материалы

- [Kotlin Tutorial](https://www.baeldung.com/kotlin)


## Содержание

- [Руководство по Coroutines](#руководство-по-coroutines)
  - [Первая корутина](#первая-корутина)
  - [Библиотека kotlinx-coroutines](#библиотека-kotlinx-coroutines)
  - [Масштабирование корутин](#масштабирование-корутин)
  - [Отмена корутин](#отмена-корутин)
  - [Таймауты](#таймауты)
  - [Параллельное выполнение с async](#параллельное-выполнение-с-async)
  - [Ленивое выполнение](#ленивое-выполнение)
- [CoroutineContext и Dispatchers](#coroutinecontext-и-dispatchers)
  - [CoroutineContext](#coroutinecontext)
  - [Элементы CoroutineContext](#элементы-coroutinecontext)
  - [CoroutineDispatcher](#coroutinedispatcher)
  - [Dispatchers.Unconfined](#dispatchersunconfined)
  - [CoroutineScope](#coroutinescope)
- [Threads против Coroutines](#threads-против-coroutines)
  - [Создание потоков в Kotlin](#создание-потоков-в-kotlin)
  - [Функция thread()](#функция-thread)
  - [Проблемы с потоками](#проблемы-с-потоками)
  - [Преимущества корутин](#преимущества-корутин)
  - [Функция launch](#функция-launch)
  - [Функция async](#функция-async)
  - [runBlocking](#runblocking)
- [runBlocking против coroutineScope](#runblocking-против-coroutinescope)
  - [Основные различия](#основные-различия)
  - [Приостановка корутин](#приостановка-корутин)
  - [Отмена корутин](#отмена-корутин-1)
- [Руководство по volatile](#руководство-по-volatile)
- [Руководство по Channel](#руководство-по-channel)
  - [Базовое использование](#базовое-использование)
  - [Типы каналов](#типы-каналов)
  - [Produce builder](#produce-builder)
  - [Broadcast channel](#broadcast-channel)
  - [Fan-out и Fan-in](#fan-out-и-fan-in)
  - [Ticker channel](#ticker-channel)
- [Облегченный параллелизм в Java и Kotlin](#облегченный-параллелизм-в-java-и-kotlin)
  - [Параллелизм](#параллелизм)
  - [Потоки пользовательского уровня](#потоки-пользовательского-уровня)
  - [Структурированный параллелизм](#структурированный-параллелизм)
  - [Корутины в Kotlin](#корутины-в-kotlin)
  - [Project Loom](#project-loom)
- [Ожидание завершения нескольких потоков (корутин)](#ожидание-завершения-нескольких-потоков-корутин)
- [Получить имя выполняемой в данный момент функции](#получить-имя-выполняемой-в-данный-момент-функции)
- [Руководство по функции yield](#руководство-по-функции-yield)
- [Вызов функции после задержки](#вызов-функции-после-задержки)
- [Создание пула потоков](#создание-пула-потоков)
- [Сравнение Coroutines и RxKotlin](#сравнение-coroutines-и-rxkotlin)
  - [Корутины](#корутины)
  - [RxKotlin](#rxkotlin)
- [Работа с Reactive Flow с MongoDB и Spring WebFlux](#работа-с-reactive-flow-с-mongodb-и-spring-webflux)
- [Flow API и холодные потоки](#flow-api-и-холодные-потоки)
  - [Создание Flow](#создание-flow)
  - [Операторы Flow](#операторы-flow)
    - [Transform operators](#transform-operators)
    - [Terminal operators](#terminal-operators)
    - [Size-limiting operators](#size-limiting-operators)
  - [Exception handling в Flow](#exception-handling-в-flow)
  - [Flow completion](#flow-completion)
  - [Buffering и conflation](#buffering-и-conflation)
- [StateFlow и SharedFlow](#stateflow-и-sharedflow)
  - [StateFlow](#stateflow)
  - [SharedFlow](#sharedflow)
  - [Replay и buffering](#replay-и-buffering)
- [Exception handling в корутинах](#exception-handling-в-корутинах)
  - [Exception propagation](#exception-propagation)
  - [CoroutineExceptionHandler](#coroutineexceptionhandler)
  - [Exception в async](#exception-в-async)
  - [SupervisorJob](#supervisorjob)
- [Продвинутые паттерны каналов](#продвинутые-паттерны-каналов)
  - [Buffered channels](#buffered-channels)
  - [Channel producers](#channel-producers)
  - [Fan-out (multiple consumers)](#fan-out-multiple-consumers)
  - [Fan-in (multiple producers)](#fan-in-multiple-producers)
  - [Select expression](#select-expression)
- [Mutex и семафоры](#mutex-и-семафоры)
  - [Mutex (Mutual exclusion)](#mutex-mutual-exclusion)
  - [Semaphore](#semaphore)
  - [Read-write mutex](#read-write-mutex)
- [Lifecycle и cleanup](#lifecycle-и-cleanup)
  - [withContext и resource management](#withcontext-и-resource-management)
  - [Coroutine lifecycle hooks](#coroutine-lifecycle-hooks)
  - [Disposable resources](#disposable-resources)
- [Timeouts и deadlines](#timeouts-и-deadlines)
  - [withTimeout](#withtimeout)
  - [withTimeoutOrNull](#withtimeoutornull)
  - [Deadline-based timeouts](#deadline-based-timeouts)
- [Тестирование корутин](#тестирование-корутин)
  - [Testing suspended functions](#testing-suspended-functions)
  - [Testing coroutine builders](#testing-coroutine-builders)
  - [Testing Flow](#testing-flow)
  - [Testing with TestCoroutineDispatcher](#testing-with-testcoroutinedispatcher)
- [Debugging concurrent code](#debugging-concurrent-code)
  - [Coroutine debugging](#coroutine-debugging)
  - [Debug output with -Dkotlinx.coroutines.debug](#debug-output-with-dkotlinxcoroutinesdebug)
  - [Stack trace recovery](#stack-trace-recovery)
  - [Thread dump analysis](#thread-dump-analysis)
- [Profiling и performance](#profiling-и-performance)
  - [CPU profiling](#cpu-profiling)
  - [Memory profiling](#memory-profiling)
  - [Benchmarking coroutines](#benchmarking-coroutines)
- [Integration с Java кодом](#integration-с-java-кодом)
  - [Calling Java from Kotlin coroutines](#calling-java-from-kotlin-coroutines)
  - [Calling Kotlin coroutines from Java](#calling-kotlin-coroutines-from-java)
  - [Bridging callback-based APIs](#bridging-callback-based-apis)
- [Migration patterns](#migration-patterns)
  - [From threads to coroutines](#from-threads-to-coroutines)
  - [From callbacks to suspend functions](#from-callbacks-to-suspend-functions)
  - [From RxJava to Flow](#from-rxjava-to-flow)
- [Best practices для корутин](#best-practices-для-корутин)
  - [Structured concurrency](#structured-concurrency)
  - [Proper resource management](#proper-resource-management)
  - [Error handling patterns](#error-handling-patterns)
  - [Testing best practices](#testing-best-practices)
- [Performance optimization](#performance-optimization)
  - [Choosing the right dispatcher](#choosing-the-right-dispatcher)
  - [Flow optimization](#flow-optimization)
  - [Channel buffering](#channel-buffering)
  - [Custom coroutine builders](#custom-coroutine-builders)
- [Common pitfalls и их избежание](#common-pitfalls-и-их-избежание)
  - [1. GlobalScope usage](#1-globalscope-usage)
  - [2. Blocking operations](#2-blocking-operations)
  - [3. Exception swallowing](#3-exception-swallowing)
  - [4. Resource leaks](#4-resource-leaks)
  - [5. Race conditions](#5-race-conditions)
- [Real-world examples](#real-world-examples)
  - [HTTP client with timeout and retry](#http-client-with-timeout-and-retry)
  - [Producer-consumer pattern](#producer-consumer-pattern)
  - [Actor pattern](#actor-pattern)
- [Интеграция с Android](#интеграция-с-android)
  - [Lifecycle-aware корутины](#lifecycle-aware-корутины)
  - [Работа с LiveData и StateFlow](#работа-с-livedata-и-stateflow)
- [Интеграция с Spring WebFlux](#интеграция-с-spring-webflux)
  - [Reactive контроллеры с корутинами](#reactive-контроллеры-с-корутинами)
  - [Реактивные репозитории](#реактивные-репозитории)
- [Тестирование корутин](#тестирование-корутин-1)
  - [Тестирование suspend функций](#тестирование-suspend-функций)
  - [Мокирование корутин](#мокирование-корутин)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Оптимизация использования корутин](#оптимизация-использования-корутин)
  - [Избегание утечек памяти](#избегание-утечек-памяти)
- [Работа с параллельными корутинами](#работа-с-параллельными-корутинами)
  - [Параллельная обработка данных](#параллельная-обработка-данных)
  - [Координация параллельных операций](#координация-параллельных-операций)
- [Производительность корутин](#производительность-корутин)
  - [Измерение производительности](#измерение-производительности)
  - [Оптимизация использования корутин](#оптимизация-использования-корутин-1)
- [Работа с корутинами в продакшене](#работа-с-корутинами-в-продакшене)
  - [Мониторинг корутин](#мониторинг-корутин)
  - [Обработка ошибок в production](#обработка-ошибок-в-production)
- [Дополнительные техники корутин](#дополнительные-техники-корутин)
  - [Работа с каналами](#работа-с-каналами)
  - [Работа с акторами](#работа-с-акторами)
- [Дополнительные техники конкурентности](#дополнительные-техники-конкурентности)
  - [Работа с Mutex и Semaphore](#работа-с-mutex-и-semaphore)
  - [Работа с Atomic операциями](#работа-с-atomic-операциями)
- [Дополнительные техники конкурентности](#дополнительные-техники-конкурентности-1)
  - [Работа с корутинами и потоками](#работа-с-корутинами-и-потоками)
  - [Работа с корутинами и RxJava](#работа-с-корутинами-и-rxjava)
- [Дополнительные техники](#дополнительные-техники)
  - [Работа с корутинами и CompletableFuture](#работа-с-корутинами-и-completablefuture)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [Итоговые рекомендации](#итоговые-рекомендации)
- [Практические примеры использования](#практические-примеры-использования)
  - [Асинхронная загрузка данных](#асинхронная-загрузка-данных)
  - [Обработка событий в реальном времени](#обработка-событий-в-реальном-времени)
  - [Параллельная обработка с использованием async/await](#параллельная-обработка-с-использованием-asyncawait)
  - [Координация корутин с использованием select](#координация-корутин-с-использованием-select)
  - [Использование Semaphore для ограничения параллелизма](#использование-semaphore-для-ограничения-параллелизма)
  - [Использование атомарных операций](#использование-атомарных-операций)
- [См. также](#см-также)

## Руководство по Coroutines

В этой статье мы рассмотрим корутины из языка **Kotlin**. Проще говоря, корутины позволяют нам плавно создавать асинхронные программы, и они основаны на концепции программирования в стиле продолжения.

Язык **Kotlin** предоставляет нам базовые конструкции, но может получить доступ к более полезным корутинам с помощью библиотеки **kotlinx-coroutines-core**. Мы рассмотрим эту библиотеку, когда поймём основные строительные блоки языка **Kotlin**.

### Первая корутина

Создадим первую корутину с помощью функции **buildSequence.**

**И давайте реализуем генератор последовательности Фибоначчи, используя эту функцию:**

```kotlin
// Sequence builder: ленивая генерация последовательности Фибоначчи
val fibonacciSeq = sequence {
    var a = 0
    var b = 1
    yield(1)
    while (true) {
        yield(a + b)
        val tmp = a + b
        a = b
        b = tmp
    }
}
```

Сигнатура функции **yield:**

```kotlin
public abstract suspend fun yield(value: T)
```

Приостановить ключевое слово означает, что эта функция может быть блокирующей. Такая функция может приостановить корутину **buildSequence**.

Приостановочные функции могут быть созданы как стандартные функции **Kotlin**, но мы должны знать, что мы можем вызывать их только из корутины. В противном случае мы получим ошибку компилятора.

Если мы приостановили вызов в **buildSequence**, этот вызов будет преобразован в выделенное состояние в конечном автомате. Корутину можно передать и присвоить переменной, как и любую другую функцию.

В сопрограмме **fibonacciSeq** у нас есть две точки приостановки. Во-первых, когда мы вызываем **yield(1)**, а во-вторых, когда мы вызываем **yield (a + b)**.

Если эта функция **yield** приводит к некоторому блокирующему вызову, текущий поток не будет блокироваться на нём. Он сможет выполнить какой-то другой код. Как только приостановленная функция завершает своё выполнение, поток может возобновить выполнение корутины **fibonacciSeq**.

**Мы можем протестировать наш код, взяв некоторые элементы из последовательности Фибоначчи:**

```kotlin
val res = fibonacciSeq
    .take(5)
    .toList()

assertEquals(res, listOf(1, 1, 2, 3, 5))
```

### Библиотека kotlinx-coroutines

Давайте посмотрим на библиотеку **kotlinx-coroutines**, в которой есть полезные конструкции, построенные поверх базовых сопрограмм.

Добавим зависимость в библиотеку **kotlinx-`coroutines-core`.**

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-core</artifactId>
    <version>1.3.9</version>
</dependency>
```

**Библиотека **kotlinx-coroutines** добавляет множество полезных конструкций, которые позволяют нам создавать асинхронные программы. Допустим, у нас есть дорогостоящая вычислительная функция, которая добавляет строку во входной список:**

```kotlin
suspend fun expensiveComputation(res: MutableList<String>) {
    delay(1000L)
    res.add("word!")
}
```

Мы можем использовать корутину запуска, которая будет выполнять эту функцию приостановки неблокирующим образом.

```kotlin
@Test
fun givenAsyncCoroutine_whenStartIt_thenShouldExecuteItInTheAsyncWay() {
    val res = mutableListOf<String>()
    runBlocking {
        launch { expensiveComputation(res)}
        res.add("Hello,")
    }
    assertEquals(res, listOf("Hello,", "word!"))
}
```

Чтобы иметь возможность протестировать наш код, мы передаём всю логику сопрограмме **runBlocking**, которая является вызовом блокировки. Поэтому наш **assertEquals()** может выполняться синхронно после кода внутри метода **runBlocking()**.

Обратите внимание, что в этом примере, хотя метод **launch()** запускается первым, это отложенное вычисление. Основной поток продолжит добавление строки «**Hello**» к списку результатов.

После односекундной задержки, которая вводится в функции **expensiveComputation()**, слово "**word**!" будет добавлено к результату.

### Масштабирование корутин

Давайте представим ситуацию, в которой мы хотим выполнить **100000** операций асинхронно. Создание такого большого количества потоков будет очень дорогостоящим и, возможно, приведёт к исключению **OutOfMemoryError**.

**К счастью, при использовании сопрограмм это не так. Мы можем выполнять столько блокирующих операций, сколько захотим. Под капотом эти операции будут обрабатываться фиксированным количеством потоков без создания лишнего потока:**

```kotlin
@Test
fun givenHugeAmountOfCoroutines_whenStartIt_thenShouldExecuteItWithoutOutOfMemory() {
    runBlocking<Unit> {
        val counter = AtomicInteger(0)
        val numberOfCoroutines = 100_000
        val jobs = List(numberOfCoroutines) {
            launch {
                delay(1L)
                counter.incrementAndGet()
            }
        }
        jobs.forEach { it.join() }
        assertEquals(counter.get(), numberOfCoroutines)
    }
}
```

Обратите внимание, что мы выполняем **100 000** сопрограмм, и каждый запуск добавляет значительную задержку. Тем не менее нет необходимости создавать слишком много потоков, поскольку эти операции выполняются асинхронно с использованием потока из общего фонового пула потоков.

### Отмена корутин

Иногда, после того как мы запустили какое-то продолжительное асинхронное вычисление, мы хотим отменить его, потому что нас больше не интересует результат.

**Когда мы запускаем наше асинхронное действие с помощью корутины **launch(),** мы можем проверить флаг **isActive**. Этот флаг устанавливается в значение **false** всякий раз, когда основной поток вызывает метод **cancel()** для экземпляра задания:**

```kotlin
@Test
fun givenCancellableJob_whenRequestForCancel_thenShouldQuit() {
    runBlocking<Unit> {
        val job = launch(Dispatchers.Default) {
            while (isActive) {
                // выполняем работу
            }
        }
        delay(1300L)
        job.cancel()
    }
}
```

Это очень элегантный и простой способ использования механизма отмены. В асинхронном действии нам нужно только проверить, равен ли флаг **isActive** ложному, и отменить нашу обработку.

### Таймауты

Когда мы запрашиваем некоторую обработку и не уверены, сколько времени займёт это вычисление, рекомендуется установить тайм-аут для такого действия. Если обработка не завершится в течение заданного тайм-аута, мы получим исключение и сможем отреагировать на него соответствующим образом.

**Например, мы можем повторить действие:**

```kotlin
@Test(expected = CancellationException::class)
fun givenAsyncAction_whenDeclareTimeout_thenShouldFinishWhenTimedOut() {
    runBlocking<Unit> {
        withTimeout(1300L) {
            repeat(1000) { i ->
                println("Some expensive computation $i...")
                delay(500L)
            }
        }
    }
}
```

Если мы не определим тайм-аут, возможно, что наш поток будет заблокирован навсегда, потому что это вычисление зависнет. Мы не можем обработать этот случай в нашем коде, если тайм-аут не определён.

### Параллельное выполнение с async

Допустим, нам нужно запустить два асинхронных действия одновременно, а потом дождаться их результатов. Если наша обработка занимает одну секунду и нам нужно выполнить эту обработку дважды, время выполнения синхронного выполнения блокировки составит две секунды.

Было бы лучше, если бы мы могли запускать оба этих действия в отдельных потоках и ждать результатов в основном потоке.

**Мы можем использовать корутину **async()** для достижения этого, запустив обработку в двух отдельных потоках одновременно:**

```kotlin
@Test
fun givenHaveTwoExpensiveAction_whenExecuteThemAsync_thenTheyShouldRunConcurrently() {
    runBlocking<Unit> {
        val delay = 1000L
        val time = measureTimeMillis {
            val one = async(Dispatchers.Default) { someExpensiveComputation(delay) }
            val two = async(Dispatchers.Default) { someExpensiveComputation(delay) }
            runBlocking {
                one.await()
                two.await()
            }
        }
        assertTrue(time < delay * 2)
    }
}
```

После выполнения двух дорогостоящих вычислений мы приостанавливаем корутину, выполняя вызов **runBlocking()**. Как только результаты один и два будут доступны, корутина возобновится, и результаты будут возвращены. Выполнение двух задач таким образом должно занять около одной секунды.

### Ленивое выполнение

**Мы можем передать **CoroutineStart.LAZY** в качестве второго аргумента метода **async()**, но это будет означать, что асинхронные вычисления не будут запущены до тех пор, пока они не будут запрошены. Поскольку мы запрашиваем вычисления в сопрограмме **runBlocking**, это означает, что вызов **two.await()** будет выполнен только после завершения **one.await()**:**

```kotlin
fun givenTwoExpensiveAction_whenExecuteThemLazy_thenTheyShouldNotConcurrently() {
    runBlocking<Unit> {
        val delay = 1000L
        val time = measureTimeMillis {
            val one = async(Dispatchers.Default, CoroutineStart.LAZY) {
                someExpensiveComputation(delay)
            }
            val two = async(Dispatchers.Default, CoroutineStart.LAZY) {
                someExpensiveComputation(delay)
            }
            one.await()
            two.await()
        }
        // время будет около 2 секунд
    }
}
```

Ленивое выполнение в этом конкретном примере приводит к тому, что наш код выполняется синхронно. Это происходит потому, что когда мы вызываем **await()**, основной поток блокируется, и только после завершения первой задачи будет запущена вторая.

Нам нужно помнить о ленивом выполнении асинхронных действий, поскольку они могут выполняться блокирующим образом.

## CoroutineContext и Dispatchers

В этом руководстве мы узнаем о **CoroutineContext** а затем продолжим рассмотрение диспетчеров как одного из важных элементов **CoroutineContext**

**Корутины** — это подпрограммы или программы, обеспечивающие совместную многозадачность. Следовательно, корутины могут быть приостановлены или возобновлены, или они могут уступить место другой сопрограмме. В **Kotlin** ключевое слово **suspend** перед функцией означает, что она приостанавливает вызывающую программу и может быть вызвана только внутри корутины.

У нас есть несколько функций построения сопрограмм: **launch** и **async** — расширения **CoroutineScope**, а также **runBlocking**

### CoroutineContext

**Каждая корутина имеет связанный **CoroutineContext**, который представляет собой индексированный набор **Element**s. Итак, что такое индексированный набор? Это смесь набора и карты, или, другими словами, это набор с уникальным ключом для каждого из элементов. Кроме того, **CoroutineContext#get** примечателен тем, что обеспечивает безопасность типов при поиске разнородных элементов:**

```kotlin
public operator fun <E : Element> get(key: Key<E>): E?
```

**Все классы сопрограмм реализуют **CoroutineScope** и имеют свойство **coroutineContext**. Поэтому мы можем получить доступ к **coroutineContext** в блоке корутины:**

```kotlin
// В runBlocking доступен coroutineContext текущей корутины
runBlocking {
    Assertions.assertNotNull(coroutineContext)
}
```

И мы можем прочитать элемент **coroutineContext**

```kotlin
runBlocking {
    Assertions.assertNotNull(coroutineContext[Job])
}
```

**CoroutineContext** неизменяем, но мы можем получить новый контекст, добавив элемент, удалив один или объединив два существующих контекста. Кроме того, контекст без какого-либо элемента может быть создан как экземпляр **EmptyCoroutineContext**.

**Мы можем объединить два **CoroutineContext** с помощью оператора плюс (+). Примечательным дизайном здесь является то, что экземпляр **Element** сам по себе является одноэлементным **CoroutineContext**. Следовательно, мы можем легко создать новый контекст, добавив элемент в контекст:**

```kotlin
// Добавление элемента в контекст корутины (оператор +)
val context = EmptyCoroutineContext
val newContext = context + CoroutineName("baeldung")

Assertions.assertTrue(newContext != context)
Assertions.assertEquals("baeldung", newContext[CoroutineName]!!.name)
```

Или мы можем удалить элемент из **CoroutineContext** вызвав **CoroutineContext**#**minusKey**

```kotlin
val context = CoroutineName("baeldung")
val newContext = context.minusKey(CoroutineName)

Assertions.assertNull(newContext[CoroutineName])
```

### Элементы CoroutineContext

**В **Kotlin** есть множество реализаций **CoroutineContext.Element** для сохранения и управления различными аспектами корутины:**

1.  Отладка: **CoroutineName**, **CoroutineId**
2.  Управление жизненным циклом: **Job** который хранит иерархию задач и может использоваться для управления жизненным циклом.
3.  Обработка исключений: **CoroutineExceptionHandler** обрабатывает возникшие исключения в сборщиках сопрограмм, таких как запуск которые не распространяют исключения
4.  Управление потоком: **ContinuationInterceptor** который прослушивает продолжение внутри корутины и перехватывает его возобновление. Реализации **CoroutineDispatcher** являются наиболее часто используемыми типами в этой категории. Более того, элементом **ContinuationInterceptor** по умолчанию является **Dispatchers.Default**

### CoroutineDispatcher

**CoroutineDispatcher** — это подтип элемента контекста **ContinuationInterceptor**, следовательно, он отвечает за определение потока (или потоков) выполнения корутины.

Когда **Kotlin** выполняет корутину, он сначала проверяет, возвращает ли **CoroutineDispatcher#isDispatchNeeded** значение **true** или нет. Если да, то **CoroutineDispatcher#dispatch** назначает поток выполнения; в противном случае **Kotlin** выполняет корутину без ограничений.

В **Kotlin** есть несколько реализаций **CoroutineDispatcher** а также несколько внутренних одноэлементных экземпляров: **DefaultScheduler**, **CommonPool**, **DefaultExecutor** и **Unconfined**

Чтобы передать предопределенный планировщик, мы можем использовать значения **kotlinx.coroutines.Dispatchers**

1.  **Dispatchers.Default** если мы не установим системное свойство **kotlinx.coroutines.scheduler** или не включим его, оно указывает на синглтон **DefaultScheduler** В противном случае он указывает на синглтон **CommonPool**
2.  **Dispatchers.Main** загружает основной диспетчер и доступен только в том случае, если в пути к классам существует требуемая зависимость.
3.  **Dispatchers.Unconfined** указатель на синглтон **Unconfined**.
4.  **Dispatchers.IO** указатель на **DefaultScheduler.IO**

**Давайте передадим диспетчер функции построения корутины:**

```kotlin
// Запуск корутины на пуле потоков по умолчанию (CPU-bound)
launch(Dispatchers.Default) {
    Assertions.assertTrue(
        coroutineContext[ContinuationInterceptor]!!
            .javaClass
            .name.contains("DefaultScheduler")
    )
}
```

Кроме того, в **ThreadPoolDispatcher.kt** есть две устаревшие общедоступные функции: **newSingleThreadContext** для выполнения одного потока и **newFixedThreadPoolContext** для назначения пула потоков. В качестве замены мы можем создать экземпляр **ExecutorService** и передать его как **CoroutineDispatcher**.

```kotlin
launch(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    Assertions.assertTrue(
        coroutineContext[ContinuationInterceptor]!!
            .javaClass
            .name.contains("ExecutorCoroutineDispatcher")
    )
}
```

**По умолчанию диспетчер наследуется от внешней **CoroutineScope** если только мы явно не передаем диспетчер в функции компоновщика:**

```kotlin
runBlocking(Executors.newSingleThreadExecutor().asCoroutineDispatcher()) {
    launch {
        Assertions.assertTrue(
            coroutineContext[ContinuationInterceptor]!!
                .javaClass
                .name.contains("ExecutorCoroutineDispatcher")
        )
        Assertions.assertTrue(Thread.currentThread().name.startsWith("pool"))
    }
}
```

### Dispatchers.Unconfined

С другой стороны, **Dispatchers.Unconfined** ссылается на внутренний объект **Unconfined**, который переопределяет **CoroutineDispatcher#isDispatchNeeded** значением **false**.

```kotlin
override fun isDispatchNeeded(context: CoroutineContext): Boolean = false
```

**Это заставляет корутину запускаться в потоке вызывающей стороны до тех пор, пока корутина не вызовет блок приостановки, а затем возобновляет поток приостанавливающей функции:**

```kotlin
runBlocking {
    launch(Dispatchers.Unconfined) {
        Assertions.assertTrue(Thread.currentThread().name.startsWith("main"))
        delay(10)
        Assertions.assertTrue(!Thread.currentThread().name.startsWith("main"))
    }
}
```

Для **Dispatchers#Unconfined** подходят корутины, которые не загружают ЦП и не обновляют какие-либо общие данные.

### CoroutineScope

Как мы поняли из предыдущего обсуждения, **CoroutineScope** — это интерфейс только с одним свойством: **coroutineContext**. Кроме того, мы можем создавать корутины, используя функции построения сопрограмм — расширения **CoroutineScope**, называемые **async** и **launch**. Обе функции-компоновщика запрашивают три параметра:

1.  **context** (необязательно): если ничего не передано, значение по умолчанию — **EmptyCoroutineContext**
2.  **coroutineStart** (необязательно): если ничего не прошло, предполагается, что **CoroutineStart.DEFAULT**. Другие доступные варианты: **LAZY**, **ATOMIC** и **UNDISPATCHED**.
3.  **suspend block:** исполняемый блок кода внутри корутины

Нас интересует аргумент контекста Чтобы создать контекст для новой корутины, функция компоновщика добавляет аргумент контекста к текущему **CoroutineScope.coroutineContext**, а затем добавляет некоторые элементы конфигурации.

Далее билдер создает экземпляр корутины из одной из реализаций **AbstractCoroutine**

1.  для запуска **StandaloneCoroutine** или **LazyStandaloneCoroutine**
2.  для асинхронного режима **DeferredCoroutine** или **LazyDeferredCoroutine**

Затем построитель передаёт новый контекст в конструктор.

**Контекст AbstractCoroutine** — это **parentContext** (контекст предыдущего шага) плюс сама корутина. Поскольку **AbstractCoroutine** является и **CoroutineScope**, и **Job**, контекст корутины содержит элемент **Job**.

```kotlin
public final override val context: CoroutineContext = parentContext + this
```

**GlobalScope** — это одноэлементный **CoroutineScope**, но без какого-либо ограниченного задания и с **EmptyCoroutineContext**. Хотя мы должны избегать его использования со сборщиками сопрограмм, его могут использовать корутины верхнего уровня или неограниченные.

## Threads против Coroutines

В этом кратком руководстве мы собираемся создавать и выполнять потоки в **Kotlin.**

Позже мы обсудим, как вообще избежать этого в пользу **Kotlin Coroutines**.

### Создание потоков в Kotlin

Создание потока в **Kotlin** аналогично созданию потока в **Java.**

**Мы могли бы либо расширить класс **Thread** (хотя это не рекомендуется, поскольку `Kotlin` не поддерживает множественное наследование):**

```kotlin
class SimpleThread: Thread() {
    public override fun run() {
        println("${Thread.currentThread()} has run.")
    }
}
```

Или мы можем реализовать интерфейс **Runnable:**

```kotlin
class SimpleRunnable: Runnable {
    public override fun run() {
        println("${Thread.currentThread()} has run.")
    }
}
```

И так же, как мы это делаем в **Java,** мы можем выполнить его, вызвав метод **start():**

```kotlin
val thread = SimpleThread()
thread.start()

val threadWithRunnable = Thread(SimpleRunnable())
threadWithRunnable.start()
```

**В качестве альтернативы, как и **Java 8**, **Kotlin** поддерживает **SAM Conversions**, поэтому мы можем воспользоваться этим и передать лямбду:**

```kotlin
// Создание потока через лямбду (Runnable)
val thread = Thread {
    println("${Thread.currentThread()} has run.")
}
thread.start()
```

### Функция thread()

**Другой способ — рассмотреть функцию **thread**(), которую предоставляет **Kotlin**:**

```kotlin
fun thread(
    start: Boolean = true,
    isDaemon: Boolean = false,
    contextClassLoader: ClassLoader? = null,
    name: String? = null,
    priority: Int = -1,
    block: () -> Unit
): Thread
```

**С помощью этой функции поток может быть создан и выполнен просто:**

```kotlin
thread(start = true) {
    println("${Thread.currentThread()} has run.")
}
```

**Функция принимает пять параметров:**

- **start** — немедленно запустить поток
- **isDaemon** — для создания потока как потока демона.
- **contextClassLoader** — загрузчик классов для загрузки классов и ресурсов.
- **name** — установить имя потока
- **priority** — установить приоритет потока

### Проблемы с потоками

Заманчиво думать, что порождение большего количества потоков может помочь нам выполнять больше задач одновременно. К сожалению, это не всегда так.

Создание слишком большого количества потоков может в некоторых ситуациях привести к снижению производительности приложения; потоки — это объекты, которые создают накладные расходы во время выделения объектов и сборки мусора.

### Преимущества корутин

Подобно потокам, корутины могут выполняться одновременно, ожидать и взаимодействовать друг с другом с той разницей, что их создание намного дешевле, чем создание потоков.

Прежде чем представить сборщиков сопрограмм, которые **Kotlin** предоставляет из коробки, мы должны обсудить контекст корутины.

Корутины всегда выполняются в некотором контексте, который представляет собой набор различных элементов.

**Основные элементы:**

- **Job** — моделирует отменяемый рабочий процесс с несколькими состояниями и жизненным циклом, кульминацией которого является его завершение.
- **Dispatchers** — определяет, какой поток или потоки использует соответствующая корутина для своего выполнения.

С помощью диспетчера мы можем ограничить выполнение корутины конкретным потоком, отправить ее в пул потоков или позволить ей работать без ограничений.

Мы увидим, как указать контекст, когда будем описывать корутины на следующих этапах.

### Функция launch

**Функция запуска -** это построитель сопрограмм, который запускает новую корутину, не блокируя текущий поток, и возвращает ссылку на корутину в виде объекта **Job:**

```kotlin
runBlocking {
    val job = launch(Dispatchers.Default) {
        println("${Thread.currentThread()} has run.")
    }
}
```

**Он имеет два необязательных параметра:**

- **context** — контекст, в котором выполняется корутина; если он не определён, он наследует контекст от **CoroutineScope**, из которого он запускается.
- **start** — параметры запуска корутины.

По умолчанию корутина немедленно запланирована для выполнения.

Обратите внимание, что приведенный выше код выполняется в общем фоновом пуле потоков, потому что мы использовали **Dispatchers.`Default`,** который запускает его в **GlobalScope.**

**В качестве альтернативы мы можем использовать **GlobalScope.launch,** который использует тот же диспетчер:**

```kotlin
val job = GlobalScope.launch {
    println("${Thread.currentThread()} has run.")
}
```

Когда мы используем **Dispatchers.Default** или **GlobalScope.launch**, мы создаём корутину верхнего уровня. Несмотря на то, что она лёгкая, она всё же потребляет некоторые ресурсы памяти во время работы.

**Вместо запуска сопрограмм в **GlobalScope,** как мы обычно делаем с потоками (потоки всегда глобальны),** мы можем запускать корутины в конкретной области действия, которую мы выполняем:**

```kotlin
runBlocking {
    val job = launch {
        println("${Thread.currentThread()} has run.")
    }
}
```

В этом случае мы запускаем новую корутину внутри построителя корутины **runBlocking** (которую мы опишем позже) без указания контекста. Таким образом, корутина наследует контекст **runBlocking**.

### Функция async

Ещё одна функция, которую **Kotlin** предоставляет для создания корутины, — это **async**.

Асинхронная функция создает новую корутину и возвращает будущий результат в виде экземпляра **Deferred<T>:**

```kotlin
val deferred = async {
    return@async "${Thread.currentThread()} has run."
}
```

**Deferred** — это неблокирующее отменяемое будущее, описывающее объект, который действует как прокси для результата, который изначально неизвестен.

**Как и при запуске, мы можем указать контекст для выполнения корутины, а также параметр запуска:**

```kotlin
val deferred = async(Dispatchers.Unconfined, CoroutineStart.LAZY) {
    println("${Thread.currentThread()} has run.")
}
```

В данном случае мы запустили корутину с помощью **Dispatchers.Unconfined**, который запускает корутины в вызывающем потоке, но только до первой точки приостановки.

Обратите внимание, что **Dispatchers.Unconfined** хорошо подходит, когда корутина не потребляет процессорное время и не обновляет какие-либо общие данные.

**Кроме того, **Kotlin** предоставляет **Dispatchers.IO**, который использует общий пул потоков, созданных по требованию:**

```kotlin
val deferred = async(Dispatchers.IO) {
    println("${Thread.currentThread()} has run.")
}
```

**Dispatchers.IO** рекомендуется, когда нам нужно выполнять интенсивные операции ввода-вывода.

### runBlocking

Ранее мы уже рассматривали **runBlocking**, но теперь давайте поговорим о нём более подробно.

**runBlocking** — это функция, которая запускает новую корутину и блокирует текущий поток до её завершения.

В качестве примера в предыдущем фрагменте мы запустили корутину, но так и не дождались результата.

Чтобы дождаться результата, мы должны вызвать метод приостановки **await():**

```kotlin
// async code goes here
runBlocking {
    val result = deferred.await()
    println(result)
}
```

**await()** — это то, что называется функцией приостановки.

Функции приостановки можно вызывать только из корутины или другой функции приостановки. По этой причине мы заключили его в вызов **runBlocking**.

Мы используем **runBlocking** в основных функциях и в тестах, чтобы мы могли связать блокирующий код с другим, написанным в стиле приостановки.

**Аналогично тому, как мы это делали в других сборщиках сопрограмм, мы можем установить контекст выполнения:**

```kotlin
runBlocking(newSingleThreadContext("dedicatedThread")) {
    val result = deferred.await()
    println(result)
}
```

Обратите внимание, что мы можем создать новый поток, в котором мы могли бы выполнить корутину. Однако выделенный поток является дорогостоящим ресурсом. И, когда он больше не нужен, мы должны освободить его или, что ещё лучше, повторно использовать его во всём приложении.

## runBlocking против coroutineScope

В этом руководстве мы сравним два метода запуска сопрограмм **Kotlin runBlocking** и **coroutineScope**

Прежде чем мы сможем использовать корутины, нам нужно добавить зависимость **kotlinx-coroutines-core** к нашему **pom.xml**

```xml
<dependency>
    <groupId>org.jetbrains.kotlinx</groupId>
    <artifactId>kotlinx-coroutines-core</artifactId>
    <version>1.6.2</version>
</dependency>
```

В следующих разделах мы рассмотрим, чем отличаются **runBlocking** и **coroutineScope** при запуске, приостановке и отмене сопрограмм.

### Основные различия

И **runBlocking**, и **coroutineScope** являются сборщиками сопрограмм, что означает, что они используются для запуска сопрограмм, но мы используем их в разных контекстах.

Когда мы используем **coroutineScope** для сборки и запуска корутины, мы создаём точку приостановки. Точки приостановки — это места в коде, где **Kotlin** может приостановить текущую корутину. Однако мы не можем создать точку приостановки, когда приостанавливать нечего, поэтому мы не можем вызывать **coroutineScope** вне области действия существующей корутины.

Иногда нам нужно запустить корутину из-за пределов существующей области корутины, например, из основного метода или модульного теста. Здесь мы будем использовать **runBlocking**, который соединяет блокирующий и приостанавливаемый код. Мы можем вызывать **runBlocking** за пределами любой существующей корутины. Как следует из названия, корутина, запускаемая **runBlocking**, блокирует текущий поток; она не создаёт точку приостановки, даже если используется внутри другой корутины.

### Приостановка корутин

В последнем разделе мы видели, как **runBlocking** и **coroutineScope** различаются тем, могут ли они приостановить содержащую их корутину, если она существует. Здесь мы рассмотрим, могут ли сами корутины, запущенные двумя сборщиками, быть приостановлены.

**Корутины, запущенные **coroutineScope**, можно приостанавливать. Чтобы увидеть это, давайте начнём с создания диспетчера сопрограмм с использованием фиксированного пула потоков с двумя потоками:**

```kotlin
val context = Executors.newFixedThreadPool(2).asCoroutineDispatcher()
```

Далее давайте определим функцию, которая запустит десять сопрограмм, каждая из которых запустит дочернюю корутину с помощью **coroutineScope**

```kotlin
fun demoWithCoroutineScope() = runBlocking {
    (1..10).forEach {
        launch(context) {
            coroutineScope {
                println("Start No.$it in coroutineScope on ${Thread.currentThread().name}")
                delay(500)
                println("End No.$it in coroutineScope on ${Thread.currentThread().name}")
            }
        }
    }
}
```

Выше мы начинаем с **runBlocking**, чтобы создать мост из нашего блокирующего кода. Изнутри блока **runBlocking** мы используем **launch** для отправки приостанавливаемых сопрограмм любому неактивному потоку в пуле потоков контекста. Наконец, мы используем **coroutineScope** для запуска корутины, которая вызовет **delay()** с интервалом **500** миллисекунд. Метод **delay()** — это функция приостановки, которая создаёт точку приостановки для корутины, запущенной **coroutineScope**.

### Отмена корутин

Разница между **runBlocking** и **coroutineScope** также проявляется при отмене корутин.

Когда мы используем **coroutineScope**, и одна из дочерних корутин отменяется, отменяются все остальные дочерние корутины, а затем **coroutineScope** выбрасывает **CancellationException**.

Когда мы используем **runBlocking**, отмена дочерней корутины не приводит к отмене **runBlocking**, и он продолжает выполнение, если только мы явно не обработаем отмену.

## Руководство по volatile

В **Kotlin**, как и в **Java**, ключевое слово **volatile** используется для гарантии видимости изменений переменной между потоками.

```kotlin
@Volatile
var flag = false
```

**@Volatile** в **Kotlin** эквивалентен **volatile** в **Java** и гарантирует, что изменения переменной будут видны всем потокам немедленно.

Однако при работе с корутинами **volatile** может быть недостаточным, и лучше использовать атомарные классы или другие примитивы синхронизации.

## Руководство по Channel

Каналы — это способ передачи данных между корутинами. Они похожи на блокирующие очереди, но являются приостанавливающими функциями.

### Базовое использование

```kotlin
val channel = Channel<Int>()

launch {
    for (x in 1..5) channel.send(x * x)
    channel.close()
}

launch {
    for (value in channel) println(value)
}
```

### Типы каналов

- **Channel<T>** — неограниченный канал
- **Channel<T>(capacity)** — канал с фиксированной емкостью
- **Channel<T>(Channel.UNLIMITED)** — неограниченный канал
- **Channel<T>(Channel.CONFLATED)** — канал, который сохраняет только последнее значение

### Produce builder

```kotlin
val channel = produce {
    for (x in 1..5) send(x * x)
}
```

### Broadcast channel

```kotlin
val broadcast = broadcastChannel<Int>(Channel.BUFFERED)
val producer = launch {
    for (x in 1..5) broadcast.send(x)
    broadcast.close()
}

val receiver1 = launch {
    for (value in broadcast.openSubscription()) {
        println("Receiver 1: $value")
    }
}
```

### Fan-out и Fan-in

**Fan-out** — это когда несколько корутин получают данные из одного канала:**

```kotlin
val producer = produce {
    for (x in 1..5) send(x)
}

repeat(3) { id ->
    launch {
        for (value in producer) {
            println("Processor $id got $value")
        }
    }
}
```

**Fan-in** — это когда несколько корутин отправляют данные в один канал:**

```kotlin
val channel = Channel<String>()

launch {
    channel.send("First")
}

launch {
    channel.send("Second")
}

repeat(2) {
    println(channel.receive())
}
channel.close()
```

### Ticker channel

**Тикерный канал** — это сопрограммный эквивалент традиционного таймера. Он выдаёт значение **Unit** с заданным регулярным интервалом. Этот тип канала удобен для выполнения задания через равные промежутки времени.

**Давайте рассмотрим пример простого сборщика цен на акции. Наша программа будет получать цену данной акции каждые пять секунд. Давайте посмотрим на реализацию с использованием канала тикера:**

```kotlin
fun stockPrice(stock: String): Double {
    log("Fetching stock price of $stock")
    return Random.nextDouble(2.0, 3.0)
}

fun main() = runBlocking {
    val tickerChannel = ticker(Duration.ofSeconds(5).toMillis())
    repeat(3) {
        tickerChannel.receive()
        log(stockPrice("TESLA"))
    }
    delay(Duration.ofSeconds(11).toMillis())
    tickerChannel.cancel()
}
```

Здесь мы видим, что новая цена акции печатается каждые пять секунд. Когда мы закончим, мы остановим канал тикера, вызвав для него метод отмены.

## Облегченный параллелизм в Java и Kotlin

В этом руководстве мы рассмотрим основные концепции параллелизма и то, как их решают различные языки программирования, в частности **Java** и **Kotlin**.

Мы сосредоточимся в первую очередь на облегченных моделях параллелизма и сравним корутины в **Kotlin** с будущими предложениями на **Java** в рамках **Project `Loom`.**

### Параллелизм

**Параллелизм** — это возможность разложить программу на компоненты, которые не зависят от порядка или частично упорядочены. Цель здесь состоит в том, чтобы несколько независимых процессов работали вместе, не влияя на результат.

В ядре операционной системы мы называем экземпляр программы процессом. Ядро изолирует процессы, назначая им разные адресные пространства в целях безопасности и отказоустойчивости. Поскольку у каждого процесса есть собственное адресное пространство, дескрипторы открытых файлов и т. д., их создание довольно затратно.

Более того, поскольку процессы не могут получить доступ к памяти друг друга, взаимодействие между процессами становится нетривиальным.

**Именно здесь потоки уровня ядра приносят облегчение при параллельном программировании:**

**Потоки** — это отдельные строки выполнения внутри процесса. Обычно процесс может иметь несколько потоков. Хотя потоки совместно используют одни и те же дескрипторы файлов и адресные пространства, они поддерживают свои собственные программные стеки. Это значительно упрощает взаимодействие между потоками.

Ядро операционной системы поддерживает и управляет потоками уровня ядра напрямую. Ядро предоставляет системные вызовы для создания и управления этими потоками извне. Однако ядро имеет полный контроль над этими потоками, включая их планирование. Это делает потоки на уровне ядра медленными и неэффективными, что приводит к дорогостоящим операциям с потоками.

### Потоки пользовательского уровня

**С другой стороны, у нас также есть потоки пользовательского уровня, которые поддерживаются в пользовательском пространстве, части системной памяти, выделенной для запущенных приложений:**

Существуют различные модели, которые отображают потоки пользовательского уровня в потоки уровня ядра, такие как **«один к одному»** или **«многие к одному»**. Но исполняющая система, такая как виртуальная машина, напрямую управляет потоками пользовательского уровня.

Ядро не знает о потоках пользовательского уровня. Следовательно, операции с потоками на уровне пользователя выполняются намного быстрее. Конечно, это требует координации между планировщиком потоков пользовательского уровня и ядром.

### Структурированный параллелизм

Типичное параллельное приложение с более чем одним путём выполнения трудно понять. Часть проблемы в том, что ему не хватает абстракции. Например, если мы вызываем функцию в таком приложении, мы не можем гарантировать, что обработка прекратится, когда фракция завершится. Это связано с тем, что функция могла породить несколько параллельных путей выполнения, о которых мы совершенно не знаем.

**Последовательный поток программы намного легче читать и писать. Конечно, для поддержки параллелизма этот поток должен разветвляться. Но гораздо проще понять, если все ветки заканчиваются обратно в основной поток:**

Таким образом, сохраняя абстракцию, нам всё равно, как функция внутренне разлагает программу. Пока всё в порядке, так как все строки выполнения заканчиваются функцией. В качестве альтернативы, области параллельных исполнений являются чисто вложенными. Это фундаментальная предпосылка структурированного параллелизма. В нём подчёркивается, что если управление разделяется на параллельные задачи, они должны снова объединиться.

Если мы увидим некоторые модели асинхронного программирования, такие как реактивное программирование, мы поймём, что добиться структурированного параллелизма сложно. На самом деле параллельное программирование в основном включает произвольные переходы, даже с более простыми примитивами, такими как потоки.

Однако мы можем добиться структурированного параллелизма в **Kotlin** с помощью такого решения, как корутины.

### Корутины в Kotlin

**Kotlin** обеспечивает поддержку облегчённых потоков в виде сопрограмм, которые реализованы в виде богатой библиотеки — **kotlinx.coroutines**. Интересно, что **JVM** не имеет встроенной поддержки облегчённой конструкции параллелизма, такой как корутина, — ну, по крайней мере, пока! Тем не менее, **Kotlin** представил корутины как экспериментальную языковую функцию довольно рано, и они стали официальными в версии **1.3**.

**Вообще говоря, корутины** — это части компьютерной программы или обобщённые подпрограммы, выполнение которых можно приостановить и возобновить в любой момент. Впервые они появились как метод в языках ассемблера ещё в **1950-х** годах. Корутины могут иметь несколько интересных применений.

Когда мы используем их для параллелизма, они кажутся похожими на потоки ядра. Однако есть тонкие различия. Например, планировщик упреждающе управляет потоками ядра, в то время как корутины добровольно уступают управление, что приводит к совместной многозадачности.

### Project Loom

**Java** имеет первоклассную поддержку параллелизма с первых дней своего существования. Однако в **Java** нет встроенной поддержки того, что мы называем облегчёнными потоками. Хотя было несколько попыток создать такую поддержку вне ядра **Java**, ни одна из них не увенчалась успехом.

Последние пару лет **OpenJDK** работал над **Project Loom**, чтобы восполнить этот пробел.

Начиная с **JDK 1.0**, класс **Thread** обеспечивает базовую абстракцию для параллелизма в **Java**. Он был предназначен для работы на всех платформах, чтобы соответствовать обещанию «напиши один раз, работай где угодно». К сожалению, в то время некоторые целевые платформы не имели встроенной поддержки потоков. Следовательно, для выполнения этого обещания в **Java** пришлось реализовать что-то, называемое зелёными потоками.

По сути, зелёные потоки — это реализация потоков, которые управляются в пользовательском пространстве и планируются виртуальной машиной. Мы уже видели общее определение таких потоков и обсуждали, как корутины в **Kotlin** или горутины в Go и подобные концепции. Хотя зелёные потоки могут различаться с точки зрения реализации, основная идея на самом деле была очень похожей.

В первые дни **Java** изо всех сил пыталась усовершенствовать реализацию зелёных потоков. Было сложно масштабировать зелёные потоки на нескольких процессорах и, следовательно, получать выгоду от параллелизма в многоядерных системах. Чтобы обойти эту проблему и упростить модель параллельного программирования, в **Java** решили отказаться от зелёных потоков в версии **1.3**.

Итак, **Java** решила отобразить каждый поток в отдельный собственный поток ядра. По сути, потоки **JVM** стали тонкой оболочкой потоков операционной системы. Это упростило модель программирования, и **Java** могла использовать преимущества параллелизма с упреждающим планированием потоков ядром на нескольких ядрах.

Модель параллелизма в **Java** на самом деле была довольно проста в использовании и была существенно улучшена с появлением **ExecutorService** и **CompletableFuture**. Это также хорошо работало в течение длительного периода времени. Однако проблема заключается в том, что параллельные приложения, написанные с использованием этой модели, сегодня сталкиваются с беспрецедентным масштабом.

Например, типичные контейнеры сервлетов написаны в модели **«поток на запрос»**. Но невозможно создать столько потоков в системе, сколько одновременных запросов мы ожидаем, что они обработают. Это требует альтернативных моделей программирования, таких как цикл событий или реактивное программирование, которые по своей сути не блокируют, но у них есть свои проблемы.

К настоящему времени нам нетрудно догадаться, что, возможно, пришло время для **Java** вернуть поддержку легковесных потоков. На самом деле это мотивация **Project Loom**. Целью этого проекта является исследование и инкубация упрощённой модели параллелизма на платформе **Java**. Идея состоит в том, чтобы создать поддержку облегчённых потоков поверх потоков **JVM** и фундаментально отделить потоки **JVM** от собственных потоков ядра.

## Ожидание завершения нескольких потоков (корутин)

**В **Kotlin** мы можем ожидать завершения нескольких корутин с помощью функции **joinAll**:**

```kotlin
val jobs = listOf(
    launch { delay(1000); println("Job 1") },
    launch { delay(2000); println("Job 2") },
    launch { delay(3000); println("Job 3") }
)

jobs.joinAll()
println("All jobs completed")
```

**Или мы можем использовать **awaitAll** для **Deferred** значений:**

```kotlin
val deferred = listOf(
    async { delay(1000); "Result 1" },
    async { delay(2000); "Result 2" },
    async { delay(3000); "Result 3" }
)

val results = deferred.awaitAll()
println(results)
```

## Получить имя выполняемой в данный момент функции

**В **Kotlin** мы можем получить имя текущей функции с помощью рефлексии:**

```kotlin
inline fun <reified T> T.getCurrentFunctionName(): String {
    return Thread.currentThread().stackTrace[1].methodName
}
```

**Или в корутине:**

```kotlin
suspend fun getCurrentCoroutineName(): String? {
    return coroutineContext[CoroutineName]?.name
}
```

## Руководство по функции yield

**Функция **yield** позволяет корутине добровольно уступить управление другим корутинам:**

```kotlin
suspend fun work() {
    repeat(5) {
        println("Working $it")
        yield()  // уступаем управление
    }
}

fun main() = runBlocking {
    val job1 = launch { work() }
    val job2 = launch { work() }
    job1.join()
    job2.join()
}
```

**yield** полезен для предотвращения монополизации потока одной корутиной.

## Вызов функции после задержки

**В **Kotlin** мы можем использовать функцию **delay** для вызова функции после задержки:**

```kotlin
suspend fun delayedCall() {
    delay(1000L)
    println("Called after 1 second")
}

fun main() = runBlocking {
    launch {
        delayedCall()
    }
}
```

**Или с помощью **Timer**:**

```kotlin
Timer().schedule(1000L) {
    println("Called after 1 second")
}
```

## Создание пула потоков

**В **Kotlin** мы можем создать пул потоков с помощью **ExecutorService** и преобразовать его в **CoroutineDispatcher**:**

```kotlin
val threadPool = Executors.newFixedThreadPool(4)
val dispatcher = threadPool.asCoroutineDispatcher()

runBlocking(dispatcher) {
    launch {
        println("Running on thread pool")
    }
}
```

## Сравнение Coroutines и RxKotlin

**Корутины и **RxKotlin** решают похожие задачи, но имеют разные подходы:**

### Корутины

- Синхронный стиль написания асинхронного кода
- Легковесные, эффективные
- Поддержка структурированного параллелизма
- Отличная интеграция с языком **Kotlin**

### RxKotlin

- Реактивное программирование с потоками данных
- Богатый набор операторов для трансформации потоков
- **Backpressure** поддержка
- Широко используется в **Android** разработке

## Работа с Reactive Flow с MongoDB и Spring WebFlux

**Kotlin** корутины могут быть интегрированы с **Spring WebFlux** для работы с реактивными потоками:**

```kotlin
@RestController
class UserController {

    @GetMapping("/users")
    suspend fun getUsers(): Flow<User> {
        return flow {
            userRepository.findAll().collect { emit(it) }
        }
    }
}
```

**С **MongoDB Reactive Driver**:**

```kotlin
suspend fun findUsers(): Flow<User> = flow {
    mongoCollection.find().collect { emit(it) }
}
```

Корутины предоставляют удобный способ работы с реактивными потоками, сохраняя синхронный стиль кода.

## Flow API и холодные потоки

**Flow** — это холодный асинхронный поток данных в **Kotlin**, который последовательно выдает значения и завершается успешно или с исключением.

### Создание Flow

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun simpleFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100) // имитация асинхронной работы
        emit(i) // emit next value
    }
}

fun main() = runBlocking {
    simpleFlow().collect { value ->
        println(value)
    }
}
```

### Операторы Flow

#### Transform operators

```kotlin
fun main() = runBlocking {
    (1..3).asFlow() // creates flow from range
        .map { it * it } // squares each number
        .filter { it > 3 } // filters out numbers <= 3
        .collect { println(it) } // collects and prints: 4, 9
}
```

#### Terminal operators

```kotlin
fun main() = runBlocking {
    val sum = (1..5).asFlow()
        .map { it * it } // 1, 4, 9, 16, 25
        .reduce { a, b -> a + b } // sum them (1 + 4 + 9 + 16 + 25 = 55)
    println(sum)
}
```

#### Size-limiting operators

```kotlin
fun main() = runBlocking {
    numbers()
        .take(2) // take only the first two
        .collect { value -> println(value) }
}

fun numbers(): Flow<Int> = flow {
    try {
        emit(1)
        emit(2)
        println("This line will not execute")
        emit(3)
    } finally {
        println("Finally in numbers")
    }
}
```

### Exception handling в Flow

```kotlin
fun main() = runBlocking {
    try {
        simpleFlow2().collect { value ->
            println(value)
            check(value <= 1) { "Collected $value" }
        }
    } catch (e: Exception) {
        println("Caught $e")
    }
}

fun simpleFlow2(): Flow<Int> = flow {
    for (i in 1..3) {
        println("Emitting $i")
        emit(i)
    }
}
```

### Flow completion

```kotlin
fun main() = runBlocking {
    (1..3).asFlow()
        .onEach { println("Emitting $it") }
        .onCompletion { println("Flow completed") }
        .collect { println("Collecting $it") }
}
```

### Buffering и conflation

```kotlin
fun main() = runBlocking {
    val time = measureTimeMillis {
        flow {
            for (i in 1..3) {
                delay(100) // pretend we are asynchronously waiting 100 ms
                emit(i) // emit next value
            }
        }
        .buffer() // buffer emissions, don't wait
        .collect { value ->
            delay(100) // pretend we are processing it for 100 ms
            println(value)
        }
    }
    println("Collected in $time ms")
}
```

## StateFlow и SharedFlow

**StateFlow** и **SharedFlow** — это горячие потоки, которые могут иметь несколько коллекторов.

### StateFlow

**StateFlow** — это наблюдаемый контейнер состояния с одним значением, который всегда имеет значение и может быть коллектирован множеством коллекторов.

```kotlin
class ViewModel {
    private val _state = MutableStateFlow(UIState())
    val state: StateFlow<UIState> = _state

    fun updateData() {
        _state.value = UIState(data = "Updated")
    }
}

data class UIState(val data: String = "Initial")

fun main() = runBlocking {
    val viewModel = ViewModel()

    // Collector 1
    launch {
        viewModel.state.collect { state ->
            println("Collector 1: ${state.data}")
        }
    }

    // Collector 2
    launch {
        viewModel.state.collect { state ->
            println("Collector 2: ${state.data}")
        }
    }

    delay(100)
    viewModel.updateData()

    delay(100)
}
```

### SharedFlow

**SharedFlow** — это горячий поток, который выдает значения множеству коллекторов в режиме **broadcast**.

```kotlin
fun main() = runBlocking {
    val sharedFlow = MutableSharedFlow<String>()

    // Collector 1
    launch {
        sharedFlow.collect { value ->
            println("Collector 1: $value")
        }
    }

    // Collector 2
    launch {
        sharedFlow.collect { value ->
            println("Collector 2: $value")
        }
    }

    delay(100)
    sharedFlow.emit("Hello") // Both collectors will receive this
    sharedFlow.emit("World") // Both collectors will receive this
}
```

### Replay и buffering

```kotlin
fun main() = runBlocking {
    val sharedFlow = MutableSharedFlow<String>(
        replay = 2, // replay last 2 values to new collectors
        extraBufferCapacity = 10 // additional buffer capacity
    )

    sharedFlow.emit("A")
    sharedFlow.emit("B")
    sharedFlow.emit("C")

    // New collector will receive "B" and "C" due to replay=2
    launch {
        sharedFlow.collect { value ->
            println("New collector: $value")
        }
    }

    delay(100)
    sharedFlow.emit("D") // All collectors receive "D"
}
```

## Exception handling в корутинах

### Exception propagation

```kotlin
fun main() = runBlocking {
    val job = GlobalScope.launch {
        try {
            throw Exception("Something went wrong")
        } catch (e: Exception) {
            println("Caught in coroutine: ${e.message}")
        }
    }
    job.join()
}
```

### CoroutineExceptionHandler

```kotlin
val handler = CoroutineExceptionHandler { _, exception ->
    println("CoroutineExceptionHandler got $exception")
}

fun main() = runBlocking {
    val job = GlobalScope.launch(handler) {
        throw AssertionError("Something went wrong")
    }
    job.join()
}
```

### Exception в async

```kotlin
fun main() = runBlocking {
    try {
        val deferred = GlobalScope.async {
            throw Exception("Something went wrong in async")
        }
        deferred.await()
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
}
```

### SupervisorJob

**SupervisorJob** позволяет дочерним корутинам завершаться с исключением, не отменяя другие дочерние корутины.

```kotlin
fun main() = runBlocking {
    val supervisor = SupervisorJob()
    with(CoroutineScope(coroutineContext + supervisor)) {
        // launch the first child -- its exception is ignored for this example
        val firstChild = launch(CoroutineExceptionHandler { _, _ -> }) {
            println("The first child is failing")
            throw AssertionError("The first child is cancelled")
        }
        // launch the second child
        val secondChild = launch {
            firstChild.join()
            // The cancellation of the first child is not propagated to the second child
            println("The first child is cancelled: ${firstChild.isCancelled}, but the second one is still active")
        }
        // wait until the second child completes
        secondChild.join()
    }
}
```

## Продвинутые паттерны каналов

### Buffered channels

```kotlin
fun main() = runBlocking {
    val channel = Channel<Int>(4) // buffer capacity of 4

    launch {
        repeat(10) {
            println("Sending $it")
            channel.send(it)
        }
        channel.close()
    }

    launch {
        for (value in channel) {
            println("Received: $value")
            delay(100) // simulate processing time
        }
    }
}
```

### Channel producers

```kotlin
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) {
        send(x++)
        delay(100)
    }
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>) = produce<Int> {
    for (x in numbers) {
        send(x * x)
    }
}

fun main() = runBlocking {
    val numbers = produceNumbers()
    val squares = square(numbers)

    repeat(5) {
        println(squares.receive())
    }

    coroutineContext.cancelChildren()
}
```

### Fan-out (multiple consumers)

```kotlin
fun CoroutineScope.processChannel(channel: ReceiveChannel<String>) = launch {
    for (msg in channel) {
        println("${coroutineContext[CoroutineName]} received: $msg")
    }
}

fun main() = runBlocking {
    val channel = Channel<String>()

    // Start multiple processors
    repeat(3) { index ->
        launch(CoroutineName("Processor $index")) {
            processChannel(channel)
        }
    }

    // Send data
    launch {
        val data = listOf("A", "B", "C", "D", "E", "F")
        for (item in data) {
            channel.send(item)
            delay(50)
        }
        channel.close()
    }
}
```

### Fan-in (multiple producers)

```kotlin
suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
    while (true) {
        delay(time)
        channel.send(s)
    }
}

fun main() = runBlocking {
    val channel = Channel<String>()

    launch { sendString(channel, "ping", 200L) }
    launch { sendString(channel, "pong", 300L) }

    repeat(6) {
        println(channel.receive())
    }

    coroutineContext.cancelChildren()
}
```

### Select expression

```kotlin
suspend fun selectExample() {
    val channels = listOf(
        Channel<String>(),
        Channel<String>(),
        Channel<String>()
    )

    // Start producers
    channels.forEachIndexed { index, channel ->
        launch {
            repeat(3) {
                delay((index + 1) * 100L)
                channel.send("Message from channel $index")
            }
            channel.close()
        }
    }

    // Select from multiple channels
    repeat(9) {
        select<Unit> {
            channels.forEach { channel ->
                channel.onReceive { value ->
                    println("Received: $value")
                }
            }
        }
    }
}

fun main() = runBlocking {
    selectExample()
}
```

## Mutex и семафоры

### Mutex (Mutual exclusion)

```kotlin
val mutex = Mutex()
var counter = 0

suspend fun increment() {
    mutex.withLock {
        counter++
    }
}

fun main() = runBlocking {
    val jobs = List(100) {
        launch {
            repeat(100) {
                increment()
            }
        }
    }
    jobs.forEach { it.join() }
    println("Counter: $counter") // Should be 10000
}
```

### Semaphore

```kotlin
val semaphore = Semaphore(2) // Allow 2 concurrent operations

suspend fun doWork(id: Int) {
    semaphore.acquire()
    try {
        println("Work $id started")
        delay(1000)
        println("Work $id finished")
    } finally {
        semaphore.release()
    }
}

fun main() = runBlocking {
    val jobs = List(5) {
        launch {
            doWork(it)
        }
    }
    jobs.forEach { it.join() }
}
```

### Read-write mutex

```kotlin
class ReadWriteResource {
    private val mutex = Mutex()
    private val readersMutex = Mutex()
    private var readers = 0
    private var value = 0

    suspend fun read(): Int {
        readersMutex.withLock {
            readers++
            if (readers == 1) {
                mutex.lock()
            }
        }
        try {
            return value
        } finally {
            readersMutex.withLock {
                readers--
                if (readers == 0) {
                    mutex.unlock()
                }
            }
        }
    }

    suspend fun write(newValue: Int) {
        mutex.withLock {
            value = newValue
        }
    }
}
```

## Lifecycle и cleanup

### withContext и resource management

```kotlin
class DatabaseConnection {
    suspend fun connect() { /* ... */ }
    suspend fun disconnect() { /* ... */ }

    suspend fun <T> use(block: suspend (DatabaseConnection) -> T): T {
        try {
            connect()
            return block(this)
        } finally {
            disconnect()
        }
    }
}

suspend fun useDatabase() = DatabaseConnection().use { conn ->
    // Use connection here
    "result"
}

fun main() = runBlocking {
    val result = useDatabase()
    println(result)
}
```

### Coroutine lifecycle hooks

```kotlin
suspend fun lifecycleExample() = coroutineScope {
    val job = launch {
        try {
            println("Coroutine started")
            delay(1000)
            println("Coroutine finished")
        } catch (e: CancellationException) {
            println("Coroutine was cancelled")
            throw e
        } finally {
            println("Cleanup in finally block")
        }
    }

    delay(500)
    job.cancelAndJoin()
}
```

### Disposable resources

```kotlin
interface Disposable {
    suspend fun dispose()
}

class Resource : Disposable {
    override suspend fun dispose() {
        println("Disposing resource")
        // cleanup logic
    }
}

suspend fun <T : Disposable, R> T.use(block: suspend (T) -> R): R {
    try {
        return block(this)
    } finally {
        dispose()
    }
}

suspend fun useResource() = Resource().use { resource ->
    // use resource
    "result"
}
```

## Timeouts и deadlines

### withTimeout

```kotlin
suspend fun longRunningTask(): String {
    delay(2000)
    return "Completed"
}

fun main() = runBlocking {
    try {
        val result = withTimeout(1000) {
            longRunningTask()
        }
        println(result)
    } catch (e: TimeoutCancellationException) {
        println("Task timed out")
    }
}
```

### withTimeoutOrNull

```kotlin
suspend fun fetchData(): String? = withTimeoutOrNull(1000) {
    delay(500)
    "Data"
}

fun main() = runBlocking {
    val data = fetchData()
    if (data == null) {
        println("Timeout occurred")
    } else {
        println("Received: $data")
    }
}
```

### Deadline-based timeouts

```kotlin
fun main() = runBlocking {
    try {
        withContext(NonCancellable) {
            val deadline = time.now() + 1000.milliseconds
            while (time.now() < deadline) {
                // do work
                delay(100)
            }
        }
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
```

## Тестирование корутин

### Testing suspended functions

```kotlin
class UserService(private val repository: UserRepository) {
    suspend fun getUser(id: Int): User? {
        delay(100) // simulate network call
        return repository.findById(id)
    }
}

@Test
fun `should return user when found`() = runBlocking {
    val repository = mockk<UserRepository>()
    val service = UserService(repository)

    val expectedUser = User(1, "John")
    coEvery { repository.findById(1) } returns expectedUser

    val result = service.getUser(1)

    assertEquals(expectedUser, result)
    coVerify { repository.findById(1) }
}
```

### Testing coroutine builders

```kotlin
class AsyncService {
    suspend fun processAsync(data: String): String {
        return withContext(Dispatchers.IO) {
            delay(1000)
            data.uppercase()
        }
    }
}

@Test
fun `should process data asynchronously`() = runBlocking {
    val service = AsyncService()

    val result = service.processAsync("hello")

    assertEquals("HELLO", result)
}
```

### Testing Flow

```kotlin
class DataService {
    fun getDataStream(): Flow<String> = flow {
        emit("A")
        delay(100)
        emit("B")
        delay(100)
        emit("C")
    }
}

@Test
fun `should emit data in correct order`() = runBlocking {
    val service = DataService()
    val results = mutableListOf<String>()

    service.getDataStream().collect { data ->
        results.add(data)
    }

    assertEquals(listOf("A", "B", "C"), results)
}
```

### Testing with TestCoroutineDispatcher

```kotlin
class TimerService(private val dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    suspend fun waitAndExecute(delay: Long, action: () -> Unit) {
        withContext(dispatcher) {
            delay(delay)
            action()
        }
    }
}

@Test
fun `should execute after delay`() = runBlocking {
    val testDispatcher = StandardTestDispatcher()
    val service = TimerService(testDispatcher)
    var executed = false

    val job = launch {
        service.waitAndExecute(1000) {
            executed = true
        }
    }

    // Advance time
    testDispatcher.scheduler.advanceTimeBy(1000)

    assertTrue(executed)
    job.cancel()
}
```

## Debugging concurrent code

### Coroutine debugging

```kotlin
fun main() = runBlocking(CoroutineName("Main")) {
    log("Starting")

    val job1 = launch(CoroutineName("Job1")) {
        log("Job1 started")
        delay(500)
        log("Job1 finished")
    }

    val job2 = launch(CoroutineName("Job2")) {
        log("Job2 started")
        delay(300)
        log("Job2 finished")
    }

    job1.join()
    job2.join()
    log("All done")
}

fun log(msg: String) {
    println("[${Thread.currentThread().name}] $msg")
}
```

### Debug output with -Dkotlinx.coroutines.debug

```bash
# Run with debug enabled
java -Dkotlinx.coroutines.debug -jar app.jar
```

### Stack trace recovery

```kotlin
suspend fun deepFunction(depth: Int): String {
    if (depth > 0) {
        return deepFunction(depth - 1)
    }
    return suspendCancellableCoroutine { cont ->
        // This will preserve the stack trace
        cont.resume("Result")
    }
}
```

### Thread dump analysis

```kotlin
fun printThreadDump() {
    val threadMXBean = ManagementFactory.getThreadMXBean()
    val threadInfos = threadMXBean.dumpAllThreads(true, true)

    for (threadInfo in threadInfos) {
        if (threadInfo.threadName.contains("kotlinx")) {
            println("Thread: ${threadInfo.threadName}")
            println("State: ${threadInfo.threadState}")
            for (stackTraceElement in threadInfo.stackTrace) {
                println("  $stackTraceElement")
            }
            println()
        }
    }
}
```

## Profiling и performance

### CPU profiling

```kotlin
suspend fun cpuIntensiveTask() {
    val list = mutableListOf<Int>()
    repeat(100000) {
        list.add(it * it)
    }
    list.sort()
}

fun main() = runBlocking {
    val time = measureTimeMillis {
        val jobs = List(4) {
            launch(Dispatchers.Default) {
                cpuIntensiveTask()
            }
        }
        jobs.forEach { it.join() }
    }
    println("Completed in $time ms")
}
```

### Memory profiling

```kotlin
suspend fun memoryIntensiveTask() {
    val largeList = mutableListOf<String>()
    repeat(10000) {
        largeList.add("String number $it".repeat(100)) // Large strings
    }
    delay(100) // Allow GC to run
    largeList.clear()
}

fun main() = runBlocking {
    println("Memory before: ${getMemoryUsage()}")

    val jobs = List(10) {
        launch {
            memoryIntensiveTask()
        }
    }
    jobs.forEach { it.join() }

    System.gc()
    println("Memory after: ${getMemoryUsage()}")
}

fun getMemoryUsage(): String {
    val runtime = Runtime.getRuntime()
    val used = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
    return "$used MB"
}
```

### Benchmarking coroutines

```kotlin
@Benchmark
fun benchmarkCoroutineVsThread() = runBlocking {
    val iterations = 1000

    // Benchmark coroutines
    val coroutineTime = measureTimeMillis {
        val jobs = List(iterations) {
            launch {
                delay(1)
            }
        }
        jobs.forEach { it.join() }
    }

    // Benchmark threads
    val threadTime = measureTimeMillis {
        val threads = List(iterations) {
            thread {
                Thread.sleep(1)
            }
        }
        threads.forEach { it.join() }
    }

    println("Coroutines: $coroutineTime ms")
    println("Threads: $threadTime ms")
}
```

## Integration с Java кодом

### Calling Java from Kotlin coroutines

```kotlin
// Java class
public class JavaService {
    public CompletableFuture<String> fetchData() {
        return CompletableFuture.supplyAsync(() -> {
            // simulate async work
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Data from Java";
        });
    }
}

// Kotlin usage
suspend fun fetchDataFromJava(service: JavaService): String {
    return service.fetchData().await()
}

fun main() = runBlocking {
    val service = JavaService()
    val data = fetchDataFromJava(service)
    println(data)
}
```

### Calling Kotlin coroutines from Java

```kotlin
// Kotlin suspend function
suspend fun kotlinAsyncOperation(): String {
    delay(1000)
    return "Result from Kotlin"
}

// Java usage
public class JavaCaller {
    public void callKotlinSuspend(CoroutineScope scope) {
        scope.launch(() -> {
            String result = kotlinAsyncOperation();
            System.out.println(result);
            return Unit.INSTANCE;
        });
    }
}
```

### Bridging callback-based APIs

```kotlin
suspend fun <T> awaitCallback(callback: (Callback<T>) -> Unit): T =
    suspendCancellableCoroutine { cont ->
        callback(object : Callback<T> {
            override fun onSuccess(result: T) {
                cont.resume(result)
            }

            override fun onError(error: Throwable) {
                cont.resumeWithException(error)
            }
        })

        cont.invokeOnCancellation {
            // Cleanup if needed
        }
    }

// Usage
interface Callback<T> {
    fun onSuccess(result: T)
    fun onError(error: Throwable)
}

fun fetchData(callback: Callback<String>) {
    // Simulate async operation
    thread {
        Thread.sleep(1000)
        callback.onSuccess("Data")
    }
}

suspend fun getData(): String = awaitCallback { callback ->
    fetchData(callback)
}
```

## Migration patterns

### From threads to coroutines

```kotlin
// Before (threads)
fun oldApproach() {
    val executor = Executors.newFixedThreadPool(4)

    val futures = (1..10).map { id ->
        executor.submit(Callable {
            Thread.sleep(100)
            "Result $id"
        })
    }

    futures.forEach { future ->
        println(future.get())
    }

    executor.shutdown()
}

// After (coroutines)
fun newApproach() = runBlocking {
    val results = (1..10).map { id ->
        async {
            delay(100)
            "Result $id"
        }
    }

    results.forEach { deferred ->
        println(deferred.await())
    }
}
```

### From callbacks to suspend functions

```kotlin
// Before (callbacks)
fun fetchUser(id: Int, callback: (User?, Throwable?) -> Unit) {
    // async operation
    thread {
        try {
            val user = database.getUser(id)
            callback(user, null)
        } catch (e: Exception) {
            callback(null, e)
        }
    }
}

// Usage
fetchUser(1) { user, error ->
    if (error != null) {
        println("Error: ${error.message}")
    } else {
        println("User: $user")
    }
}

// After (suspend function)
suspend fun fetchUser(id: Int): User {
    return withContext(Dispatchers.IO) {
        database.getUser(id)
    }
}

// Usage
fun main() = runBlocking {
    try {
        val user = fetchUser(1)
        println("User: $user")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
```

### From RxJava to Flow

```kotlin
// RxJava approach
fun getDataRx(): Observable<String> {
    return Observable.create { emitter ->
        // async operation
        thread {
            try {
                val data = fetchData()
                emitter.onNext(data)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }
}

// Usage
getDataRx()
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(
        { data -> println(data) },
        { error -> println("Error: ${error.message}") }
    )

// Flow approach
fun getDataFlow(): Flow<String> = flow {
    val data = withContext(Dispatchers.IO) {
        fetchData()
    }
    emit(data)
}

// Usage
fun main() = runBlocking {
    getDataFlow()
        .collect { data ->
            println(data)
        }
}
```

## Best practices для корутин

### Structured concurrency

```kotlin
suspend fun processUserData(userId: Int) = coroutineScope {
    val profile = async { fetchUserProfile(userId) }
    val posts = async { fetchUserPosts(userId) }

    UserData(
        profile = profile.await(),
        posts = posts.await()
    )
}

fun main() = runBlocking {
    try {
        val userData = processUserData(123)
        println("User data loaded: $userData")
    } catch (e: Exception) {
        println("Failed to load user data: ${e.message}")
    }
}
```

### Proper resource management

```kotlin
class DatabaseConnection : AutoCloseable {
    suspend fun connect() { /* ... */ }

    override fun close() {
        // cleanup
    }
}

suspend fun <T> DatabaseConnection.use(block: suspend (DatabaseConnection) -> T): T {
    try {
        connect()
        return block(this)
    } finally {
        close()
    }
}

suspend fun queryUser(id: Int): User = DatabaseConnection().use { conn ->
    conn.executeQuery("SELECT * FROM users WHERE id = ?", id)
}
```

### Error handling patterns

```kotlin
suspend fun safeApiCall(): Result<String> = try {
    val response = apiService.call()
    Result.success(response)
} catch (e: IOException) {
    Result.failure(e)
} catch (e: HttpException) {
    Result.failure(e)
}

suspend fun processData(): String? {
    return safeApiCall().getOrNull()?.let { data ->
        processData(data)
    }
}

fun main() = runBlocking {
    val result = processData()
    result?.let { println("Processed: $it") }
        ?: println("Processing failed")
}
```

### Testing best practices

```kotlin
class UserServiceTest {
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `should handle timeout gracefully`() = runBlocking(testDispatcher) {
        val service = UserService(timeoutMs = 100)

        // Advance time before making call
        testDispatcher.scheduler.advanceTimeBy(50)

        val result = service.fetchData()

        // Verify timeout behavior
        assertTrue(result.isFailure())
    }

    @Test
    fun `should retry on failure`() = runBlocking(testDispatcher) {
        var attempts = 0
        val mockApi = mockk<ApiService> {
            coEvery { call() } answers {
                attempts++
                if (attempts < 3) throw IOException("Network error")
                else "Success"
            }
        }

        val service = UserService(api = mockApi, maxRetries = 3)
        val result = service.callWithRetry()

        assertEquals("Success", result)
        coVerify(exactly = 3) { mockApi.call() }
    }
}
```

## Performance optimization

### Choosing the right dispatcher

```kotlin
// CPU-bound work
suspend fun cpuIntensive() = withContext(Dispatchers.Default) {
    // Use default dispatcher for CPU work
    heavyComputation()
}

// I/O-bound work
suspend fun ioBound() = withContext(Dispatchers.IO) {
    // Use IO dispatcher for I/O operations
    database.query()
}

// Main thread work (Android)
suspend fun uiUpdate() = withContext(Dispatchers.Main) {
    // Update UI on main thread
    updateViews()
}
```

### Flow optimization

```kotlin
// Inefficient - creates intermediate collections
val result = flowOf(1, 2, 3, 4, 5)
    .map { it * 2 }
    .filter { it > 5 }
    .toList()

// Optimized - no intermediate collections
val result = flowOf(1, 2, 3, 4, 5)
    .map { it * 2 }
    .filter { it > 5 }
    .toList()
```

### Channel buffering

```kotlin
// Unbuffered channel - slow
val channel1 = Channel<Int>()

// Buffered channel - faster
val channel2 = Channel<Int>(capacity = 100)

// Conflated channel - keeps only latest value
val channel3 = Channel<Int>(capacity = Channel.CONFLATED)
```

### Custom coroutine builders

```kotlin
fun CoroutineScope.launchWithRetry(
    times: Int = 3,
    initialDelay: Long = 100,
    maxDelay: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> Unit
) = launch {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            block()
            return@launch
        } catch (e: Exception) {
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }
    block() // last attempt
}

// Usage
launchWithRetry {
    riskyOperation()
}
```

## Common pitfalls и их избежание

### 1. GlobalScope usage

```kotlin
// Плохо - утечка корутины
fun badExample() {
    GlobalScope.launch {
        work()
    }
}

// Хорошо - структурированная конкуренция
suspend fun goodExample() = coroutineScope {
    launch {
        work()
    }
}
```

### 2. Blocking operations

```kotlin
// Плохо - блокирует поток
suspend fun badBlocking() {
    Thread.sleep(1000) // blocks thread
}

// Хорошо - неблокирующая задержка
suspend fun goodNonBlocking() {
    delay(1000) // suspends coroutine
}
```

### 3. Exception swallowing

```kotlin
// Плохо - глотает исключения
val job = launch {
    try {
        riskyOperation()
    } catch (e: Exception) {
        // silently ignore
    }
}

// Хорошо - правильная обработка
val job = launch {
    try {
        riskyOperation()
    } catch (e: Exception) {
        handleException(e)
        throw e // re-throw if needed
    }
}
```

### 4. Resource leaks

```kotlin
// Плохо - утечка ресурсов
suspend fun badResource() {
    val file = File("data.txt")
    file.readText() // no cleanup
}

// Хорошо - использование use
suspend fun goodResource() = File("data.txt").use {
    it.readText()
}
```

### 5. Race conditions

```kotlin
// Плохо - race condition
var counter = 0

suspend fun increment() {
    counter++ // not thread-safe
}

// Хорошо - атомарные операции или mutex
val counter = AtomicInteger(0)

suspend fun increment() {
    counter.incrementAndGet()
}
```

## Real-world examples

### HTTP client with timeout and retry

```kotlin
class HttpClient(
    private val client: OkHttpClient = OkHttpClient()
) {
    suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()

        withTimeout(5000) {
            client.newCall(request).await()
        }
    }

    private suspend fun Call.await(): String = suspendCancellableCoroutine { cont ->
        enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                cont.resume(response.body?.string() ?: "")
            }

            override fun onFailure(call: Call, e: IOException) {
                cont.resumeWithException(e)
            }
        })

        cont.invokeOnCancellation {
            cancel()
        }
    }
}

suspend fun fetchWithRetry(client: HttpClient, url: String, maxRetries: Int = 3): String {
    var lastException: Exception? = null

    repeat(maxRetries) { attempt ->
        try {
            return client.get(url)
        } catch (e: Exception) {
            lastException = e
            if (attempt < maxRetries - 1) {
                delay((attempt + 1) * 1000L) // exponential backoff
            }
        }
    }

    throw lastException ?: RuntimeException("Unknown error")
}
```

### Producer-consumer pattern

```kotlin
class ProducerConsumer<T>(
    private val bufferSize: Int = 10,
    private val producers: Int = 2,
    private val consumers: Int = 2
) {
    private val channel = Channel<T>(bufferSize)

    suspend fun produce(item: T) {
        channel.send(item)
    }

    suspend fun consume(): T {
        return channel.receive()
    }

    fun close() {
        channel.close()
    }
}

fun main() = runBlocking {
    val pc = ProducerConsumer<String>()

    // Start producers
    repeat(pc.producers) { producerId ->
        launch {
            repeat(5) { itemId ->
                val item = "Item $itemId from producer $producerId"
                pc.produce(item)
                println("Produced: $item")
                delay(100)
            }
        }
    }

    // Start consumers
    repeat(pc.consumers) { consumerId ->
        launch {
            try {
                while (true) {
                    val item = pc.consume()
                    println("Consumer $consumerId consumed: $item")
                    delay(150)
                }
            } catch (e: ClosedReceiveChannelException) {
                println("Consumer $consumerId finished")
            }
        }
    }

    delay(3000)
    pc.close()
}
```

### Actor pattern

```kotlin
sealed class Message
data class AddItem(val item: String) : Message()
data class RemoveItem(val item: String) : Message()
object GetItems : Message()
object Clear : Message()

class ShoppingCartActor : CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val mailbox = Channel<Message>()
    private val items = mutableListOf<String>()

    init {
        launch {
            for (message in mailbox) {
                processMessage(message)
            }
        }
    }

    private fun processMessage(message: Message) {
        when (message) {
            is AddItem -> items.add(message.item)
            is RemoveItem -> items.remove(message.item)
            is GetItems -> println("Current items: $items")
            is Clear -> items.clear()
        }
    }

    suspend fun send(message: Message) {
        mailbox.send(message)
    }

    fun close() {
        mailbox.close()
    }
}

fun main() = runBlocking {
    val cart = ShoppingCartActor()

    cart.send(AddItem("Milk"))
    cart.send(AddItem("Bread"))
    cart.send(GetItems)

    delay(100)

    cart.send(RemoveItem("Milk"))
    cart.send(GetItems)

    delay(100)
    cart.close()
}
```

Это исчерпывающее руководство охватывает все аспекты конкурентности в **Kotlin**, от основных концепций до продвинутых паттернов и лучших практик. Корутины предоставляют мощный и эффективный способ написания асинхронного кода, сохраняя простоту и читаемость.

## Интеграция с Android

### Lifecycle-aware корутины

**В **Android** важно учитывать **lifecycle** компонентов при работе с корутинами:**

```kotlin
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // lifecycleScope автоматически отменяется при уничтожении Activity
        lifecycleScope.launch {
            loadData()
        }

        // lifecycleScope в onCreate
        lifecycleScope.launchWhenCreated {
            initializeUI()
        }

        // lifecycleScope в onResume
        lifecycleScope.launchWhenResumed {
            refreshData()
        }
    }
}

class MyViewModel : ViewModel() {
    init {
        // viewModelScope автоматически отменяется при очистке ViewModel
        viewModelScope.launch {
            loadInitialData()
        }
    }
}
```

**Lifecycle-aware** корутины автоматически отменяются при уничтожении компонентов, что предотвращает утечки памяти и неожиданное поведение.

### Работа с LiveData и StateFlow

**Интеграция корутин с **LiveData** и **StateFlow** для реактивных `UI`:**

```kotlin
class UserViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        viewModelScope.launch {
            loadUsers()
        }
    }

    private suspend fun loadUsers() {
        _users.value = userRepository.getUsers()
    }

    fun refreshUsers() {
        viewModelScope.launch {
            _users.value = userRepository.refreshUsers()
        }
    }
}

// Использование в Activity/Fragment
lifecycleScope.launch {
    viewModel.users.collect { users ->
        updateUI(users)
    }
}
```

**StateFlow** и **LiveData** обеспечивают реактивное обновление `UI` при изменении данных, что делает работу с асинхронными данными более удобной.

## Интеграция с Spring WebFlux

### Reactive контроллеры с корутинами

**Корутины могут использоваться с **Spring WebFlux** для создания реактивных контроллеров:**

```kotlin
@RestController
class UserController(private val userService: UserService) {
    @GetMapping("/users")
    suspend fun getUsers(): List<User> {
        return userService.getAllUsers()
    }

    @GetMapping("/users/{id}")
    suspend fun getUser(@PathVariable id: Long): ResponseEntity<User> {
        return userService.findById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/users")
    suspend fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val saved = userService.save(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(saved)
    }
}
```

**Suspend** функции в **Spring** позволяют писать реактивные контроллеры с использованием корутин, что делает код более читаемым.

### Реактивные репозитории

**Работа с реактивными репозиториями через корутины:**

```kotlin
interface ReactiveUserRepository : ReactiveCrudRepository<User, Long> {
    fun findByName(name: String): Mono<User>
    fun findByAgeGreaterThan(age: Int): Flux<User>
}

@Service
class UserService(private val repository: ReactiveUserRepository) {
    suspend fun findUserByName(name: String): User? {
        return repository.findByName(name).awaitSingleOrNull()
    }

    suspend fun findUsersByAge(age: Int): List<User> {
        return repository.findByAgeGreaterThan(age).toList().awaitSingle()
    }
}
```

Использование корутин с реактивными репозиториями упрощает работу с базами данных в реактивных приложениях.

## Тестирование корутин

### Тестирование suspend функций

**Тестирование **suspend** функций требует использования **runTest**:**

```kotlin
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.StandardTestDispatcher

class UserServiceTest {
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun testGetUser() = runTest(testDispatcher) {
        val service = UserService(mockRepository)
        val user = service.getUser(1)

        assertEquals("Alice", user?.name)
    }

    @Test
    fun testConcurrentOperations() = runTest {
        val service = UserService(mockRepository)

        val results = coroutineScope {
            listOf(
                async { service.getUser(1) },
                async { service.getUser(2) },
                async { service.getUser(3) }
            ).awaitAll()
        }

        assertEquals(3, results.size)
    }
}
```

Тестирование **suspend** функций с использованием **runTest** позволяет писать надежные тесты для асинхронного кода.

### Мокирование корутин

**Мокирование корутин для тестирования:**

```kotlin
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class UserServiceTest {
    private val repository = mockk<UserRepository>()
    private val service = UserService(repository)

    @Test
    fun testSaveUser() = runTest {
        val user = User(name = "Alice")

        coEvery { repository.save(user) } returns user.copy(id = 1)

        val saved = service.saveUser(user)

        assertEquals(1, saved.id)
        coVerify { repository.save(user) }
    }
}
```

Мокирование корутин с использованием **MockK** позволяет изолировать тестируемый код и проверять взаимодействие с зависимостями.

## Производительность и оптимизация

### Оптимизация использования корутин

**Оптимизация использования корутин для лучшей производительности:**

```kotlin
// Используйте coroutineScope для параллельных операций
suspend fun parallelProcessing(items: List<Item>) = coroutineScope {
    items.map { item ->
        async { processItem(item) }
    }.awaitAll()
}

// Используйте flow для обработки потоков данных
fun processStream(items: List<Item>): Flow<Result> = flow {
    items.forEach { item ->
        emit(processItem(item))
    }
}.flowOn(Dispatchers.Default)

// Используйте channel для коммуникации между корутинами
suspend fun producerConsumer() = coroutineScope {
    val channel = Channel<Item>(Channel.UNLIMITED)

    launch {
        items.forEach { channel.send(it) }
        channel.close()
    }

    launch {
        for (item in channel) {
            processItem(item)
        }
    }
}
```

Правильное использование корутин, **flow** и **channel** улучшает производительность и эффективность использования ресурсов.

### Избегание утечек памяти

**Предотвращение утечек памяти при работе с корутинами:**

```kotlin
// Используйте lifecycleScope для Activity/Fragment
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            loadData()  // Автоматически отменяется при уничтожении Activity
        }
    }
}

// Используйте viewModelScope для ViewModel
class MyViewModel : ViewModel() {
    fun loadData() {
        viewModelScope.launch {
            // Автоматически отменяется при очистке ViewModel
            fetchData()
        }
    }
}

// Всегда проверяйте isActive в длительных операциях
suspend fun longRunningTask() {
    while (isActive) {
        // Работа
        delay(100)
    }
}
```

Правильное управление **scope** корутин предотвращает утечки памяти и обеспечивает корректную отмену операций.

Этот файл содержит полное руководство по конкурентности в **Kotlin**, покрывающее все основные аспекты от базовых концепций до интеграции с различными платформами, тестирования и оптимизации производительности.

## Работа с параллельными корутинами

### Параллельная обработка данных

**Использование корутин для параллельной обработки данных:**

```kotlin
// Параллельная обработка списка
suspend fun processInParallel(items: List<Item>): List<ProcessedItem> = coroutineScope {
    items.map { item ->
        async {
            processItem(item)
        }
    }.awaitAll()
}

// Параллельная обработка с ограничением количества
suspend fun processInParallelLimited(
    items: List<Item>,
    maxConcurrency: Int = 10
): List<ProcessedItem> = coroutineScope {
    val semaphore = Semaphore(maxConcurrency)

    items.map { item ->
        async {
            semaphore.withPermit {
                processItem(item)
            }
        }
    }.awaitAll()
}

// Параллельная обработка с обработкой ошибок
suspend fun processInParallelWithErrors(items: List<Item>): List<Result<ProcessedItem>> = coroutineScope {
    items.map { item ->
        async {
            try {
                Result.success(processItem(item))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }.awaitAll()
}
```

Параллельная обработка данных с использованием корутин позволяет эффективно использовать ресурсы системы и улучшать производительность.

### Координация параллельных операций

**Координация нескольких параллельных операций:**

```kotlin
// Ожидание завершения всех операций
suspend fun waitForAll() = coroutineScope {
    val job1 = launch { task1() }
    val job2 = launch { task2() }
    val job3 = launch { task3() }

    // Ожидание всех корутин
    joinAll(job1, job2, job3)
}

// Ожидание первой завершившейся операции
suspend fun waitForFirst() = coroutineScope {
    val deferred1 = async { task1() }
    val deferred2 = async { task2() }
    val deferred3 = async { task3() }

    // Выбор первой завершившейся
    val result = select {
        deferred1.onAwait { it }
        deferred2.onAwait { it }
        deferred3.onAwait { it }
    }

    // Отмена остальных
    deferred1.cancel()
    deferred2.cancel()
    deferred3.cancel()

    result
}

// Ожидание завершения с таймаутом
suspend fun waitWithTimeout() = withTimeout(5000) {
    val result1 = async { task1() }
    val result2 = async { task2() }

    result1.await() + result2.await()
}
```

Координация параллельных операций позволяет управлять выполнением нескольких задач одновременно и контролировать их завершение.

## Производительность корутин

### Измерение производительности

**Измерение производительности корутин для оптимизации:**

```kotlin
// Измерение времени выполнения корутины
suspend fun measureCoroutineTime(block: suspend () -> Unit): Long {
    val startTime = System.nanoTime()
    block()
    return System.nanoTime() - startTime
}

// Использование
val duration = measureCoroutineTime {
    processData()
}
println("Execution time: ${duration / 1_000_000}ms")

// Профилирование параллельных операций
suspend fun profileParallelOperations() = coroutineScope {
    val startTime = System.nanoTime()

    val results = listOf(
        async { task1() },
        async { task2() },
        async { task3() }
    ).awaitAll()

    val duration = System.nanoTime() - startTime
    println("Total time: ${duration / 1_000_000}ms")
    println("Average time: ${duration / 3 / 1_000_000}ms per task")
}
```

Измерение производительности помогает выявлять узкие места и оптимизировать выполнение корутин.

### Оптимизация использования корутин

**Оптимизация использования корутин для лучшей производительности:**

```kotlin
// Использование правильного dispatcher
suspend fun optimizeWithDispatcher() = withContext(Dispatchers.Default) {
    // CPU-интенсивные операции
    computeHeavyTask()
}

// Batch обработка для улучшения производительности
suspend fun batchProcessing(items: List<Item>, batchSize: Int = 100) {
    items.chunked(batchSize).forEach { batch ->
        coroutineScope {
            batch.forEach { item ->
                launch {
                    processItem(item)
                }
            }
        }
    }
}

// Использование ограничения количества параллельных операций
suspend fun limitedParallelProcessing(
    items: List<Item>,
    maxConcurrency: Int = 10
) = coroutineScope {
    val semaphore = Semaphore(maxConcurrency)

    items.map { item ->
        async {
            semaphore.withPermit {
                processItem(item)
            }
        }
    }.awaitAll()
}
```

Оптимизация использования корутин улучшает производительность и эффективность использования ресурсов.

## Работа с корутинами в продакшене

### Мониторинг корутин

**Мониторинг состояния корутин в **production**:**

```kotlin
import kotlinx.coroutines.debug.CoroutineName
import kotlinx.coroutines.debug.DebugProbes

// Включение мониторинга корутин
fun enableCoroutineMonitoring() {
    DebugProbes.install()
    System.setProperty("kotlinx.coroutines.debug", "on")
}

// Получение информации о корутинах
fun getCoroutinesInfo(): List<CoroutineInfo> {
    val dump = DebugProbes.dumpCoroutinesInfo()
    return dump.map { info ->
        CoroutineInfo(
            name = info.name,
            state = info.state,
            stackTrace = info.stackTrace
        )
    }
}

data class CoroutineInfo(
    val name: String?,
    val state: String,
    val stackTrace: List<StackTraceElement>
)

// Мониторинг использования ресурсов
class CoroutineResourceMonitor {
    private val activeCoroutines = AtomicInteger(0)
    private val completedCoroutines = AtomicLong(0)
    private val failedCoroutines = AtomicLong(0)

    fun trackCoroutine() {
        activeCoroutines.incrementAndGet()
    }

    fun trackCompletion() {
        activeCoroutines.decrementAndGet()
        completedCoroutines.incrementAndGet()
    }

    fun trackFailure() {
        activeCoroutines.decrementAndGet()
        failedCoroutines.incrementAndGet()
    }

    fun getMetrics(): Map<String, Any> {
        return mapOf(
            "active" to activeCoroutines.get(),
            "completed" to completedCoroutines.get(),
            "failed" to failedCoroutines.get(),
            "total" to completedCoroutines.get() + failedCoroutines.get()
        )
    }
}
```

Мониторинг корутин позволяет отслеживать состояние выполнения и выявлять проблемы в **production**.

### Обработка ошибок в production

**Продвинутая обработка ошибок для **production**:**

```kotlin
// Глобальная обработка ошибок корутин
class GlobalCoroutineExceptionHandler : CoroutineExceptionHandler {
    override val key = CoroutineExceptionHandler.Key

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        logger.error("Uncaught exception in coroutine", exception)

        // Отправка в систему мониторинга
        sendToMonitoring(exception)

        // Уведомление разработчиков
        notifyDevelopers(exception)
    }

    private fun sendToMonitoring(exception: Throwable) {
        // Интеграция с системой мониторинга
        monitoringService.recordException(exception)
    }

    private fun notifyDevelopers(exception: Throwable) {
        // Уведомление через email/Slack
        notificationService.notify("Critical error: ${exception.message}", exception)
    }
}

// Использование
val handler = GlobalCoroutineExceptionHandler()
val scope = CoroutineScope(SupervisorJob() + handler)

scope.launch {
    // Корутина с глобальной обработкой ошибок
    performOperation()
}

// Создание устойчивых корутин
fun <T> launchResilient(
    scope: CoroutineScope,
    block: suspend () -> T
): Job {
    return scope.launch {
        try {
            block()
        } catch (e: CancellationException) {
            throw e  // Не обрабатываем отмену
        } catch (e: Exception) {
            logger.error("Error in resilient coroutine", e)
            // Восстановление или fallback
            handleError(e)
        }
    }
}
```

Правильная обработка ошибок в **production** критична для стабильности приложения и быстрого реагирования на проблемы.

## Дополнительные техники корутин

### Работа с каналами

**Использование каналов для коммуникации между корутинами:**

```kotlin
import kotlinx.coroutines.channels.*

// Базовое использование каналов
suspend fun channelExample() = coroutineScope {
    val channel = Channel<Int>()

    launch {
        for (x in 1..5) {
            channel.send(x)
        }
        channel.close()
    }

    for (value in channel) {
        println(value)
    }
}

// Producer и Consumer паттерн
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) {
        send(x++)
        delay(100)
    }
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>) = produce<Int> {
    for (x in numbers) {
        send(x * x)
    }
}

// Использование
fun main() = runBlocking {
    val numbers = produceNumbers()
    val squares = square(numbers)

    repeat(5) {
        println(squares.receive())
    }

    coroutineContext.cancelChildren()
}

// Buffered каналы
val bufferedChannel = Channel<Int>(10)  // Буфер на 10 элементов

// Broadcast каналы
val broadcastChannel = BroadcastChannel<Int>(10)
val receiver1 = broadcastChannel.openSubscription()
val receiver2 = broadcastChannel.openSubscription()

broadcastChannel.send(1)  // Оба получателя получат значение
```

Каналы позволяют эффективно обмениваться данными между корутинами.

### Работа с акторами

**Использование акторов для изоляции состояния:**

```kotlin
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

Акторы позволяют изолировать состояние и обрабатывать сообщения последовательно.

Этот файл содержит полное руководство по конкурентности в **Kotlin**, покрывающее все основные аспекты от базовых концепций до интеграции с различными платформами, тестирования, оптимизации производительности, работы с параллельными корутинами, координации операций, измерения производительности, оптимизации использования корутин, мониторинга, обработки ошибок, работы с каналами и акторами.

## Дополнительные техники конкурентности

### Работа с Mutex и Semaphore

**Использование **Mutex** и **Semaphore** для синхронизации:**

```kotlin
import kotlinx.coroutines.sync.*

// Mutex для взаимного исключения
class Counter {
    private var value = 0
    private val mutex = Mutex()

    suspend fun increment() {
        mutex.withLock {
            value++
        }
    }

    suspend fun getValue(): Int {
        return mutex.withLock {
            value
        }
    }
}

// Semaphore для ограничения параллелизма
class ResourcePool(private val maxConcurrency: Int) {
    private val semaphore = Semaphore(maxConcurrency)

    suspend fun <T> useResource(block: suspend () -> T): T {
        return semaphore.withPermit {
            block()
        }
    }
}

// Использование
val pool = ResourcePool(5)
repeat(10) {
    launch {
        pool.useResource {
            processResource()
        }
    }
}
```

**Mutex** и **Semaphore** позволяют контролировать доступ к общим ресурсам и ограничивать параллелизм.

### Работа с Atomic операциями

**Использование атомарных операций:**

```kotlin
import java.util.concurrent.atomic.*

// Atomic операции
class AtomicCounter {
    private val value = AtomicInteger(0)

    fun increment() {
        value.incrementAndGet()
    }

    fun getValue(): Int {
        return value.get()
    }

    fun compareAndSet(expected: Int, update: Int): Boolean {
        return value.compareAndSet(expected, update)
    }
}

// Atomic reference
class AtomicReferenceExample {
    private val reference = AtomicReference<String>("initial")

    fun update(newValue: String) {
        reference.set(newValue)
    }

    fun getValue(): String {
        return reference.get()
    }

    fun compareAndSet(expected: String, update: String): Boolean {
        return reference.compareAndSet(expected, update)
    }
}
```

Атомарные операции обеспечивают **thread-safe** доступ к переменным без использования блокировок.

Этот файл содержит полное руководство по конкурентности в **Kotlin**, покрывающее все основные аспекты от базовых концепций до интеграции с различными платформами, тестирования, оптимизации производительности, работы с параллельными корутинами, координации операций, измерения производительности, оптимизации использования корутин, мониторинга, обработки ошибок, работы с каналами, акторами, **Mutex**, **Semaphore** и атомарными операциями.

## Дополнительные техники конкурентности

### Работа с корутинами и потоками

**Интеграция корутин с **Java** потоками:**

```kotlin
import java.util.concurrent.*

// Преобразование CompletableFuture в корутину
suspend fun <T> CompletableFuture<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        whenComplete { result, throwable ->
            if (throwable == null) {
                cont.resume(result)
            } else {
                cont.resumeWithException(throwable)
            }
        }
    }
}

// Использование
val future = CompletableFuture.supplyAsync {
    performOperation()
}
val result = future.await()

// Преобразование корутины в CompletableFuture
fun <T> CoroutineScope.asyncToFuture(block: suspend () -> T): CompletableFuture<T> {
    val future = CompletableFuture<T>()

    launch {
        try {
            val result = block()
            future.complete(result)
        } catch (e: Exception) {
            future.completeExceptionally(e)
        }
    }

    return future
}
```

Интеграция корутин с **Java** потоками позволяет использовать корутины в существующих **Java** приложениях.

### Работа с корутинами и RxJava

**Интеграция корутин с **RxJava**:**

```kotlin
import io.reactivex.rxjava3.core.*

// Преобразование Observable в Flow
fun <T> Observable<T>.asFlow(): Flow<T> = flow {
    subscribe(
        { value -> emit(value) },
        { error -> throw error },
        { }
    )
}

// Преобразование Flow в Observable
fun <T> Flow<T>.asObservable(): Observable<T> = Observable.create { emitter ->
    val job = CoroutineScope(Dispatchers.Default).launch {
        try {
            collect { value ->
                emitter.onNext(value)
            }
            emitter.onComplete()
        } catch (e: Exception) {
            emitter.onError(e)
        }
    }

    emitter.setCancellable { job.cancel() }
}
```

Интеграция корутин с **RxJava** позволяет постепенно мигрировать с **RxJava** на корутины.

Этот файл содержит полное руководство по конкурентности в **Kotlin**, покрывающее все основные аспекты от базовых концепций до интеграции с различными платформами, тестирования, оптимизации производительности, работы с параллельными корутинами, координации операций, измерения производительности, оптимизации использования корутин, мониторинга, обработки ошибок, работы с каналами, акторами, **Mutex**, **Semaphore**, атомарными операциями, интеграцией с **Java** потоками и **RxJava**.

## Дополнительные техники

### Работа с корутинами и CompletableFuture

**Интеграция корутин с **CompletableFuture**:**

```kotlin
import java.util.concurrent.CompletableFuture

// Преобразование CompletableFuture в корутину
suspend fun <T> CompletableFuture<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        whenComplete { result, throwable ->
            if (throwable == null) {
                cont.resume(result)
            } else {
                cont.resumeWithException(throwable)
            }
        }
    }
}

// Преобразование корутины в CompletableFuture
fun <T> CoroutineScope.asyncToFuture(block: suspend () -> T): CompletableFuture<T> {
    val future = CompletableFuture<T>()

    launch {
        try {
            val result = block()
            future.complete(result)
        } catch (e: Exception) {
            future.completeExceptionally(e)
        }
    }

    return future
}

// Использование
suspend fun main() {
    val future = CompletableFuture.supplyAsync {
        performOperation()
    }
    val result = future.await()

    val kotlinFuture = asyncToFuture {
        performKotlinOperation()
    }
    val kotlinResult = kotlinFuture.get()
}
```

Интеграция с **CompletableFuture** позволяет использовать корутины в существующих **Java** приложениях.

Этот файл содержит полное руководство по конкурентности в **Kotlin**, покрывающее все основные аспекты от базовых концепций до интеграции с различными платформами, тестирования, оптимизации производительности, работы с параллельными корутинами, координации операций, измерения производительности, оптимизации использования корутин, мониторинга, обработки ошибок, работы с каналами, акторами, **Mutex**, **Semaphore**, атомарными операциями, интеграцией с **Java** потоками, **RxJava** и **CompletableFuture**.


## Решение проблем

Типичные проблемы и решения см. в официальной документации (блок «Полезные ссылки» в начале документа).

## Частые вопросы

Ответы на частые вопросы по теме см. в разделах «Введение» и «Лучшие практики» в документе.

## Заключение

Корутины в **Kotlin** предоставляют мощный и элегантный способ работы с асинхронным и конкурентным кодом. Понимание основных концепций корутин, их интеграции с различными платформами, техник оптимизации и работы с каналами, акторами и другими инструментами позволяет создавать эффективные и масштабируемые приложения.

Этот файл содержит полное руководство по конкурентности в **Kotlin**, покрывающее все основные аспекты от базовых концепций до интеграции с различными платформами, тестирования, оптимизации производительности, работы с параллельными корутинами, координации операций, измерения производительности, оптимизации использования корутин, мониторинга, обработки ошибок, работы с каналами, акторами, **Mutex**, **Semaphore**, атомарными операциями, интеграцией с **Java** потоками, **RxJava**, **CompletableFuture** и заключение.

## Дополнительные ресурсы

**Для дальнейшего изучения конкурентности в **Kotlin** рекомендуется:**

- **Kotlin Coroutines Guide**: **https**://**kotlinlang.org**/**docs**/**coroutines-guide.html**
- **Kotlin Channels**: **https**://**kotlinlang.org**/**docs**/**channels.html**
- **Kotlin Flow**: **https**://**kotlinlang.org**/**docs**/**flow.html**
- **Coroutines Best Practices**: **https**://**kotlinlang.org**/**docs**/**coroutines-basics.html**
- **Structured Concurrency**: **https**://**kotlinlang.org**/**docs**/**coroutines-basics.html**#**structured-concurrency**

Этот файл содержит полное руководство по конкурентности в **Kotlin**, покрывающее все основные аспекты от базовых концепций до интеграции с различными платформами, тестирования, оптимизации производительности, работы с параллельными корутинами, координации операций, измерения производительности, оптимизации использования корутин, мониторинга, обработки ошибок, работы с каналами, акторами, **Mutex**, **Semaphore**, атомарными операциями, интеграцией с **Java** потоками, **RxJava**, **CompletableFuture**, заключение и дополнительные ресурсы.

## Итоговые рекомендации

**При работе с корутинами рекомендуется:**

1. Использовать **structured concurrency** для управления жизненным циклом корутин
2. Правильно выбирать **dispatcher** для различных типов операций
3. Обрабатывать ошибки с помощью **try-catch** и **SupervisorJob**
4. Использовать каналы для коммуникации между корутинами
5. Тестировать корутины с использованием **runTest**

Этот файл содержит полное руководство по конкурентности в **Kotlin**, покрывающее все основные аспекты от базовых концепций до интеграции с различными платформами, тестирования, оптимизации производительности, работы с параллельными корутинами, координации операций, измерения производительности, оптимизации использования корутин, мониторинга, обработки ошибок, работы с каналами, акторами, **Mutex**, **Semaphore**, атомарными операциями, интеграцией с **Java** потоками, **RxJava**, **CompletableFuture**, заключение, дополнительные ресурсы и итоговые рекомендации.

## Практические примеры использования

### Асинхронная загрузка данных

**Пример использования корутин для асинхронной загрузки данных:**

```kotlin
suspend fun loadUserData(userId: Long): UserData = coroutineScope {
    val userDeferred = async { userRepository.getUser(userId) }
    val postsDeferred = async { postRepository.getPostsByUser(userId) }
    val commentsDeferred = async { commentRepository.getCommentsByUser(userId) }

    UserData(
        user = userDeferred.await(),
        posts = postsDeferred.await(),
        comments = commentsDeferred.await()
    )
}
```

Параллельная загрузка данных позволяет значительно улучшить производительность приложения.

### Обработка событий в реальном времени

**Пример обработки событий с использованием каналов:**

```kotlin
class EventProcessor {
    private val eventChannel = Channel<Event>(Channel.UNLIMITED)

    suspend fun processEvents() {
        for (event in eventChannel) {
            when (event) {
                is UserEvent -> handleUserEvent(event)
                is SystemEvent -> handleSystemEvent(event)
            }
        }
    }

    fun sendEvent(event: Event) {
        eventChannel.trySend(event)
    }
}
```

Каналы позволяют эффективно обрабатывать потоки событий в реальном времени.

### Параллельная обработка с использованием async/await

**Пример параллельной обработки данных:**

```kotlin
suspend fun processMultipleUsers(userIds: List<Long>): List<UserData> = coroutineScope {
    userIds.map { userId ->
        async {
            val user = userRepository.getUser(userId)
            val posts = postRepository.getPostsByUser(userId)
            val comments = commentRepository.getCommentsByUser(userId)
            UserData(user, posts, comments)
        }
    }.awaitAll()
}

// Использование
val userIds = listOf(1L, 2L, 3L, 4L, 5L)
val results = processMultipleUsers(userIds)
```

Использование **async**/**await** позволяет эффективно распараллеливать независимые операции.

### Координация корутин с использованием select

**Пример использования **select** для координации нескольких корутин:**

```kotlin
suspend fun selectFromChannels(
    channel1: ReceiveChannel<String>,
    channel2: ReceiveChannel<Int>
): String = select {
    channel1.onReceive { value ->
        "Received string: $value"
    }
    channel2.onReceive { value ->
        "Received int: $value"
    }
}
```

**Select** позволяет обрабатывать данные из нескольких источников одновременно.

### Использование Semaphore для ограничения параллелизма

**Пример использования **Semaphore**:**

```kotlin
class ResourcePool(private val maxConcurrent: Int) {
    private val semaphore = Semaphore(maxConcurrent)

    suspend fun <T> useResource(block: suspend () -> T): T {
        semaphore.acquire()
        return try {
            block()
        } finally {
            semaphore.release()
        }
    }
}

// Использование
val pool = ResourcePool(3)
coroutineScope {
    repeat(10) { i ->
        launch {
            pool.useResource {
                println("Task $i using resource")
                delay(1000)
            }
        }
    }
}
```

**Semaphore** позволяет ограничивать количество одновременно выполняемых операций.

### Использование атомарных операций

**Пример использования атомарных переменных:**

```kotlin
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class AtomicCounter {
    private val count = AtomicInteger(0)

    fun increment(): Int {
        return count.incrementAndGet()
    }

    fun get(): Int {
        return count.get()
    }
}

class AtomicState<T>(initialValue: T) {
    private val state = AtomicReference(initialValue)

    fun update(transform: (T) -> T): T {
        return state.updateAndGet(transform)
    }

    fun get(): T {
        return state.get()
    }
}

// Использование
val counter = AtomicCounter()
coroutineScope {
    repeat(100) {
        launch {
            counter.increment()
        }
    }
}
```

Атомарные операции обеспечивают потокобезопасность без блокировок.

Этот файл содержит полное руководство по конкурентности в **Kotlin**, покрывающее все основные аспекты от базовых концепций до интеграции с различными платформами, тестирования, оптимизации производительности, работы с параллельными корутинами, координации операций, измерения производительности, оптимизации использования корутин, мониторинга, обработки ошибок, работы с каналами, акторами, **Mutex**, **Semaphore**, атомарными операциями, интеграцией с **Java** потоками, **RxJava**, **CompletableFuture**, практические примеры использования, включая параллельную обработку, **select**, **Semaphore** и атомарные операции, заключение, дополнительные ресурсы и итоговые рекомендации.

## См. также

- [Kotlin Another](kotlin-another.md)
- [Основы Kotlin — Полное руководство](kotlin-basics.md)
- [Kotlin Collections: Grouping and Aggregation](kotlin-collections-grouping.md)
- [Kotlin Collections: List](kotlin-collections-list.md)
- [Kotlin Collections: Map](kotlin-collections-map.md)
