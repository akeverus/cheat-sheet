---
title: "Вопросы на собеседовании: Kotlin Flow"
description: "Kotlin Flow — асинхронные потоки данных: cold vs hot, StateFlow, SharedFlow, операторы flatMap/combine/zip, backpressure, flowOn, тестирование с Turbine"
tags:
  - interview
  - kotlin
  - kotlin-flow-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Kotlin Flow"
  - "Kotlin Flow interview"
  - "Kotlin Flow собеседование"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Kotlin Flow`

`Kotlin Flow` — асинхронный холодный поток данных из `kotlinx.coroutines`. Построен поверх корутин и реализует паттерн реактивных потоков без зависимости от RxJava. Активно спрашивается в Kotlin backend и Android интервью.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Kotlin Flow Guide](https://kotlinlang.org/docs/flow.html) — официальная документация
- [Coroutines Flow API](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) — API reference
- [Baeldung: Kotlin Flow](https://www.baeldung.com/kotlin/flow) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое Kotlin Flow и как он связан с корутинами?](#q1-что-такое-kotlin-flow-и-как-он-связан-с-корутинами)
- [Q2. (!) Чем Cold Flow отличается от Hot Flow?](#q2-чем-cold-flow-отличается-от-hot-flow)
- [Q3. Какие основные операторы Flow вы знаете?](#q3-какие-основные-операторы-flow-вы-знаете)

**Hot Flows**
- [Q4. (!) Что такое StateFlow и когда его использовать?](#q4-что-такое-stateflow-и-когда-его-использовать)
- [Q5. (!) Что такое SharedFlow и чем отличается от StateFlow?](#q5-что-такое-sharedflow-и-чем-отличается-от-stateflow)

**Обработка ошибок и backpressure**
- [Q6. Как обрабатывать ошибки в Flow?](#q6-как-обрабатывать-ошибки-в-flow)
- [Q7. Как работает backpressure в Kotlin Flow?](#q7-как-работает-backpressure-в-kotlin-flow)
- [Q8. Что такое channelFlow и callbackFlow?](#q8-что-такое-channelflow-и-callbackflow)

**Тестирование**
- [Q9. Как тестировать Kotlin Flow?](#q9-как-тестировать-kotlin-flow)

**Операторы и контекст**
- [Q10. Чем Flow отличается от RxJava Observable?](#q10-чем-flow-отличается-от-rxjava-observable)
- [Q11. (!) Как использовать flowOn для смены контекста?](#q11-как-использовать-flowon-для-смены-контекста)
- [Q12. (!) Что такое flatMapLatest, flatMapMerge, flatMapConcat?](#q12-что-такое-flatmaplatest-flatmapmerge-flatmapconcat)
- [Q13. Как интегрировать Kotlin Flow со Spring WebFlux?](#q13-как-интегрировать-kotlin-flow-со-spring-webflux)

**Продвинутые темы**
- [Q14. Как избежать memory leak при использовании SharedFlow?](#q14-как-избежать-memory-leak-при-использовании-sharedflow)
- [Q15. Что произойдёт при исключении внутри flow {} без catch?](#q15-что-произойдёт-при-исключении-внутри-flow--без-catch)
- [Q16. Как работают zip и combine?](#q16-как-работают-zip-и-combine)
- [Q17. Что такое scan и runningFold?](#q17-что-такое-scan-и-runningfold)

## Q1. Что такое Kotlin Flow и как он связан с корутинами?

**Kotlin Flow** — асинхронный холодный (cold) поток данных из библиотеки `kotlinx.coroutines`. Построен поверх корутин: каждый элемент собирается в контексте корутины-коллектора.

```kotlin
// Базовый пример
val numbersFlow: Flow<Int> = flow {
    for (i in 1..5) {
        delay(100)    // неблокирующая пауза
        emit(i)       // отправка элемента в поток
    }
}

// Сбор потока (терминальная операция)
launch {
    numbersFlow.collect { value ->
        println("Received: $value")
    }
}
```

**Cold flow**: код в `flow { }` не выполняется до вызова `collect`. Каждый новый коллектор запускает поток заново.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Чем Cold Flow отличается от Hot Flow? Частая ошибка в реальном коде.

| Характеристика | Cold Flow | Hot Flow (StateFlow/SharedFlow) |
|----------------|-----------|----------------------------------|
| Производитель | Запускается при `collect` | Работает независимо от коллекторов |
| Коллекторов | Каждый получает все элементы | Видят только текущие/будущие |
| Примеры | `flow { }`, `channelFlow` | `StateFlow`, `SharedFlow` |
| Аналог Rx | `Observable` (cold) | `BehaviorSubject`, `PublishSubject` |

```kotlin
// Cold flow — каждый collect запускает заново
val cold = flow { emit(Random.nextInt()) }
cold.collect { println(it) }  // число X
cold.collect { println(it) }  // другое число Y

// Hot flow — общее состояние
val hot = MutableStateFlow(0)
hot.value = 42
launch { hot.collect { println(it) } }  // получит 42, затем обновления
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Какие основные операторы Flow вы знаете? Частая ошибка в реальном коде.

```kotlin
val flow = flowOf(1, 2, 3, 4, 5)

// Трансформация
flow.map { it * 2 }                    // [2, 4, 6, 8, 10]
flow.flatMapMerge { v -> flow { emit(v * 2); emit(v * 3) } }  // конкурентная

// Фильтрация
flow.filter { it % 2 == 0 }            // [2, 4]
flow.take(3)                           // [1, 2, 3]
flow.distinct()                        // убирает дубли

// Комбинирование
flow1.zip(flow2) { a, b -> a + b }     // попарное объединение (ждёт оба)
flow1.combine(flow2) { a, b -> a + b } // последнее значение каждого потока

// Управление временем
flow.debounce(300)                     // ждёт 300ms паузу (поиск по мере ввода)
flow.throttleFirst(1000)               // не чаще раза в секунду
flow.buffer(16)                        // буфер 16 элементов (backpressure)

// Сбор
flow.toList()                          // List<T>
flow.first()                           // первый элемент
flow.reduce { acc, v -> acc + v }      // свёртка
flow.fold(0) { acc, v -> acc + v }     // свёртка с начальным значением
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Что такое StateFlow и когда его использовать? Частая ошибка в реальном коде.

`StateFlow` — **hot flow** с одним текущим значением. Гарантирует, что коллектор всегда получит последнее состояние.

```kotlin
class OrderViewModel(private val repo: OrderRepository) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    fun loadOrders() {
        viewModelScope.launch {
            repo.getOrders()
                .collect { _orders.value = it }
        }
    }
}

// Подписка
viewModel.orders.collect { orders ->
    updateUI(orders)
}
```

**Особенности**:
- Всегда имеет начальное значение.
- Отличается семантикой равенства: `emit(value)` игнорируется, если `value == currentValue`.
- Аналог Android `LiveData` без привязки к жизненному циклу.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Что такое SharedFlow и чем отличается от StateFlow? Частая ошибка в реальном коде.

`SharedFlow` — hot flow без обязательного начального значения, с настраиваемым буфером воспроизведения.

```kotlin
// SharedFlow для событий (one-shot: каждый коллектор получает каждое событие)
private val _events = MutableSharedFlow<UiEvent>(
    replay = 0,         // коллекторы не видят прошлые события
    extraBufferCapacity = 16,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
val events: SharedFlow<UiEvent> = _events.asSharedFlow()

fun sendEvent(event: UiEvent) {
    viewModelScope.launch { _events.emit(event) }
}
```

| Критерий | StateFlow | SharedFlow |
|----------|-----------|------------|
| Начальное значение | Обязательно | Нет |
| replay | Всегда 1 | Настраивается (0..N) |
| Назначение | Состояние (state) | События (events) |
| `collect` без `emit` | Получает текущее значение | Ждёт следующий `emit` |

**Правило**: `StateFlow` — для данных (список, статус загрузки). `SharedFlow` — для one-shot событий (навигация, toast).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Как обрабатывать ошибки в Flow? Частая ошибка в реальном коде.

```kotlin
// catch — перехватывает upstream исключения
flow {
    emit(fetchData())  // может выбросить исключение
}.catch { e ->
    emit(emptyList())  // fallback значение
    log.error("Error", e)
}.collect { data -> updateUI(data) }

// onCompletion — выполняется всегда (успех и ошибка)
flow.onCompletion { cause ->
    if (cause != null) log.error("Flow failed", cause)
    else log.info("Flow completed")
}.collect { ... }

// retry — повторная попытка при ошибке
flow.retry(3) { e -> e is IOException }
    .catch { e -> emit(emptyList()) }
    .collect { ... }

// retryWhen — retry с задержкой
flow.retryWhen { cause, attempt ->
    if (cause is IOException && attempt < 3) {
        delay(1000L * (attempt + 1))  // экспоненциальный backoff
        true  // повторить
    } else false  // не повторять
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Как работает backpressure в Kotlin Flow? Частая ошибка в реальном коде.

В Kotlin Flow backpressure управляется через стратегии обработки переполнения буфера:

```kotlin
// buffer — буферизует до N элементов, производитель продолжает
flow.buffer(64)
    .collect { slowConsumer(it) }

// conflate — только последнее значение (пропускает промежуточные)
flow.conflate()
    .collect { processLatest(it) }

// collectLatest — отменяет предыдущую обработку при новом элементе
flow.collectLatest { value ->
    delay(100)  // долгая обработка
    process(value)  // если придёт новый элемент до завершения — отменится
}
```

Flow по умолчанию синхронный (suspend функции): производитель приостанавливается, пока коллектор не готов. Это встроенный backpressure без явного буфера.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Что такое channelFlow и callbackFlow? Частая ошибка в реальном коде.

```kotlin
// channelFlow — для конкурентной записи в поток из разных корутин
val merged = channelFlow {
    launch { flow1.collect { send(it) } }
    launch { flow2.collect { send(it) } }
}

// callbackFlow — мост между callback API и Flow
fun observeConnectivity(): Flow<Boolean> = callbackFlow {
    val listener = NetworkCallback { connected ->
        trySend(connected)  // не блокирующий send
    }
    connectivityManager.registerCallback(listener)

    awaitClose { connectivityManager.unregisterCallback(listener) }
    // awaitClose блокирует до отмены коллектора
}
```

`callbackFlow` — стандартный способ оборачивания listener-based API (Android LocationManager, Firebase, WebSocket) в Flow.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Как тестировать Kotlin Flow? Частая ошибка в реальном коде.

```kotlin
// Turbine — библиотека для тестирования Flow
@Test
fun `should emit orders in sequence`() = runTest {
    val viewModel = OrderViewModel(fakeRepository)

    viewModel.orders.test {
        val initial = awaitItem()
        assertThat(initial).isEmpty()

        viewModel.loadOrders()

        val loaded = awaitItem()
        assertThat(loaded).hasSize(3)

        cancelAndIgnoreRemainingEvents()
    }
}
```

```kotlin
// Без Turbine — через toList()
@Test
fun `flow should emit correct values`() = runTest {
    val flow = flowOf(1, 2, 3).map { it * 2 }

    val result = flow.toList()
    assertThat(result).containsExactly(2, 4, 6)
}

// Тестирование StateFlow
@Test
fun `state should update after load`() = runTest {
    val vm = OrderViewModel(mockRepo)
    val states = mutableListOf<List<Order>>()

    val job = launch { vm.orders.toList(states) }
    vm.loadOrders()
    advanceUntilIdle()
    job.cancel()

    assertThat(states.last()).isNotEmpty()
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Чем Flow отличается от RxJava Observable? Частая ошибка в реальном коде.

| Критерий | Kotlin Flow | RxJava Observable |
|----------|-------------|-------------------|
| Язык | Kotlin (coroutines) | Java/Kotlin |
| Отмена | `cancel()` корутины | `Disposable.dispose()` |
| Контекст | `flowOn(Dispatcher)` | `observeOn()` / `subscribeOn()` |
| Backpressure | Встроен (suspend) | `Flowable` vs `Observable` |
| Hot stream | `StateFlow`/`SharedFlow` | `Subject` / `ConnectableObservable` |
| Размер библиотеки | ~100KB | ~3MB |
| Тестирование | `Turbine` + `runTest` | `TestObserver` |

Kotlin Flow предпочтительнее для нового Kotlin-кода. RxJava — если проект уже использует RxJava или нужна более богатая коллекция операторов.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Как использовать flowOn для смены контекста? Частая ошибка в реальном коде.

```kotlin
// flowOn меняет контекст выполнения UPSTREAM операторов
val result = flow {
    emit(heavyComputation())  // выполняется на IO
}
.map { transform(it) }        // тоже на IO (до flowOn)
.flowOn(Dispatchers.IO)       // ← меняет контекст всего выше
.filter { it.isNotEmpty() }   // выполняется на основном потоке
.collect { updateUI(it) }     // запускается в текущей корутине

// Несколько flowOn для разных этапов
flow { emit(readFromDisk()) }
    .flowOn(Dispatchers.IO)
    .map { parse(it) }
    .flowOn(Dispatchers.Default)  // CPU-интенсивный парсинг
    .collect { show(it) }          // на Main
```

`flowOn` создаёт внутренний канал между двумя частями пайплайна. Отличается от `withContext` — не переключает контекст для текущего блока кода.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Что такое flatMapLatest, flatMapMerge, flatMapConcat? Частая ошибка в реальном коде.

```kotlin
// flatMapLatest — отменяет предыдущую трансформацию при новом элементе
searchQuery.flatMapLatest { query ->
    searchApi.search(query)  // предыдущий запрос отменяется
}

// flatMapMerge — конкурентный (по умолчанию 16 параллельных)
flow.flatMapMerge(concurrency = 4) { id ->
    fetchById(id)  // до 4 параллельных запросов
}

// flatMapConcat — последовательный (ждёт завершения каждого)
flow.flatMapConcat { id ->
    fetchById(id)  // строго по очереди
}
```

| Оператор | Поведение | Применение |
|----------|-----------|------------|
| `flatMapLatest` | Отмена предыдущего | Поисковые запросы |
| `flatMapMerge` | Параллельно | Независимые запросы |
| `flatMapConcat` | Последовательно | Зависимые операции |


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Как интегрировать Kotlin Flow со Spring WebFlux? Частая ошибка в реальном коде.

```kotlin
// Spring WebFlux поддерживает Flow напрямую
@RestController
@RequestMapping("/orders")
class OrderController(private val service: OrderService) {

    @GetMapping
    fun getOrders(): Flow<OrderDto> = service.streamOrders()

    @GetMapping("/{id}/events", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamEvents(@PathVariable id: String): Flow<OrderEvent> =
        service.getOrderEvents(id)
}

// Repository с R2DBC
interface OrderRepository : CoroutineCrudRepository<Order, Long> {
    fun findByStatus(status: OrderStatus): Flow<Order>
}
```

`CoroutineCrudRepository` — Spring Data расширение для корутин/Flow: `findById` возвращает `Order?` (suspend), `findAll` — `Flow<Order>`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Как избежать memory leak при использовании SharedFlow? Частая ошибка в реальном коде.

```kotlin
// ПРОБЛЕМА: scope живёт дольше, чем коллектор
class MyActivity : AppCompatActivity() {
    override fun onCreate(...) {
        GlobalScope.launch {  // утечка!
            viewModel.events.collect { handleEvent(it) }
        }
    }
}

// ПРАВИЛЬНО: привязка к жизненному циклу
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.events.collect { handleEvent(it) }
    }
}

// ПРАВИЛЬНО: в корутине с явной отменой
val job = viewModelScope.launch {
    sharedFlow.collect { process(it) }
}
override fun onStop() { job.cancel() }
```

`repeatOnLifecycle` — стандартный Android-паттерн для безопасного сбора Flow с автоматической паузой/возобновлением.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Что произойдёт при исключении внутри flow { } без catch? Частая ошибка в реальном коде.

```kotlin
val flow = flow {
    emit(1)
    throw RuntimeException("Oops!")
    emit(2)  // не будет выполнено
}

// Исключение распространяется до коллектора
try {
    flow.collect { println(it) }  // напечатает 1, затем бросит исключение
} catch (e: RuntimeException) {
    println("Caught: ${e.message}")  // Caught: Oops!
}

// catch оператор ловит UPSTREAM исключения, но НЕ exceptions в самом collect
flow.catch { e -> emit(-1) }
    .collect { println(it) }  // 1, -1

// islandFlow — исключения в collect НЕ ловятся catch
flow.catch { emit(-1) }
    .collect {
        if (it == 1) throw IllegalStateException("in collect!")  // НЕ перехватывается catch
    }
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Как работают zip и combine? Частая ошибка в реальном коде.

```kotlin
val flow1 = flowOf(1, 2, 3)
val flow2 = flowOf("A", "B", "C", "D")

// zip — попарное объединение, ждёт оба потока
flow1.zip(flow2) { num, letter -> "$num$letter" }
    .collect { println(it) }
// 1A, 2B, 3C (завершается когда короткий поток закончится)

// combine — последнее значение каждого потока
val numbers = MutableStateFlow(0)
val letters = MutableStateFlow("A")
numbers.combine(letters) { n, l -> "$n$l" }
    .collect { println(it) }
// 0A, затем при numbers.value = 1 → "1A", при letters.value = "B" → "1B"
```

| Оператор | Поведение | Применение |
|---|---|---|
| `zip` | Попарно, ждёт оба потока | Объединение парных результатов |
| `combine` | Последнее значение обоих | Комбинирование состояний |

`zip` — "двухрядная молния" (пара per pair). `combine` — "любое изменение → пересчёт".


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. Что такое scan и runningFold? Частая ошибка в реальном коде.

`scan`/`runningFold` — накапливающие операторы, похожие на `reduce`, но **эмитируют каждый промежуточный результат**.

```kotlin
// scan — эмитирует накопленный результат после каждого элемента
flowOf(1, 2, 3, 4, 5)
    .scan(0) { accumulator, value -> accumulator + value }
    .collect { println(it) }
// 0, 1, 3, 6, 10, 15  (начальное значение + каждый шаг)

// runningFold — псевдоним scan
flowOf(1, 2, 3)
    .runningFold(emptyList<Int>()) { acc, value -> acc + value }
    .collect { println(it) }
// [], [1], [1,2], [1,2,3]
```

**Применение:**
- Накопленная статистика (running total, running average)
- История изменений состояния
- Прогрессивное построение списка

---

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Kotlin Coroutines](kotlin-coroutines-interview.md) — suspend функции, CoroutineScope, Job, Dispatcher Частая ошибка в реальном коде.
- [Kotlin](kotlin-interview.md) — основы языка, null safety, data classes
- [Spring WebFlux](../../frameworks/spring/spring-webflux-interview.md) — реактивный стек Spring, Mono/Flux vs Flow
- [Reactive Streams](../../reactive/reactive-streams-interview.md) — спецификация backpressure (Publisher/Subscriber/Subscription)
- [Project Reactor](../../reactive/project-reactor-interview.md) — Reactor (Java): Mono, Flux — аналог Flow
- [RxJava](../../reactive/rxjava-interview.md) — сравнение Flow vs Observable/Flowable
- [Kotlin Serialization](kotlin-serialization-interview.md) — сериализация данных в Flow pipeline
- [Spring R2DBC](../../frameworks/spring/spring-r2dbc-interview.md) — реактивная работа с БД, возвращает Flow
