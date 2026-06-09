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
updated: "2026-05-14"
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
- [Q1. Что такое Kotlin Flow и как он связан с корутинами?](#q1-что-такое-kotlin-flow-и-как-он-связан-с-корутинами)
- [Q2. Чем Cold Flow отличается от Hot Flow?](#q2-чем-cold-flow-отличается-от-hot-flow)
- [Q3. Какие основные операторы Flow вы знаете?](#q3-какие-основные-операторы-flow-вы-знаете)

**Hot Flows**
- [Q4. Что такое StateFlow и когда его использовать?](#q4-что-такое-stateflow-и-когда-его-использовать)
- [Q5. Что такое SharedFlow и чем отличается от StateFlow?](#q5-что-такое-sharedflow-и-чем-отличается-от-stateflow)

**Обработка ошибок и backpressure**
- [Q6. Как обрабатывать ошибки в Flow?](#q6-как-обрабатывать-ошибки-в-flow)
- [Q7. Как работает backpressure в Kotlin Flow?](#q7-как-работает-backpressure-в-kotlin-flow)
- [Q8. Что такое channelFlow и callbackFlow?](#q8-что-такое-channelflow-и-callbackflow)

**Тестирование**
- [Q9. Как тестировать Kotlin Flow?](#q9-как-тестировать-kotlin-flow)

**Операторы и контекст**
- [Q10. Чем Flow отличается от RxJava Observable?](#q10-чем-flow-отличается-от-rxjava-observable)
- [Q11. Как использовать flowOn для смены контекста?](#q11-как-использовать-flowon-для-смены-контекста)
- [Q12. Что такое flatMapLatest, flatMapMerge, flatMapConcat?](#q12-что-такое-flatmaplatest-flatmapmerge-flatmapconcat)
- [Q13. Как интегрировать Kotlin Flow со Spring WebFlux?](#q13-как-интегрировать-kotlin-flow-со-spring-webflux)

**Продвинутые темы**
- [Q14. Как избежать memory leak при использовании SharedFlow?](#q14-как-избежать-memory-leak-при-использовании-sharedflow)
- [Q15. Что произойдёт при исключении внутри flow { } без catch?](#q15-что-произойдёт-при-исключении-внутри-flow---без-catch)
- [Q16. Как работают zip и combine?](#q16-как-работают-zip-и-combine)
- [Q17. Что такое scan и runningFold?](#q17-что-такое-scan-и-runningfold)

## Q1. Что такое Kotlin Flow и как он связан с корутинами?

**Kotlin Flow** — асинхронный холодный (cold) поток данных из библиотеки `kotlinx.coroutines`. Это способ вернуть из suspend-функции не одно значение, а последовательность значений во времени — аналог `List`, но элементы появляются асинхронно и по одному.

Связь с корутинами прямая: внутри `flow { }` можно вызывать suspend-функции (`delay`, сетевые запросы), а сам поток собирается в контексте корутины-коллектора. Поэтому Flow не нужен отдельный механизм потоков и отмены — он наследует их у корутин.

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

**Почему холодный (cold):** код внутри `flow { }` не выполняется, пока кто-то не вызовет терминальный оператор `collect`. Каждый новый коллектор запускает блок заново и получает собственную независимую последовательность — поток не хранит состояние между подписками.

**Ключевые точки:**
- `emit()` — отправка элемента; вызывается только из suspend-контекста производителя.
- `collect()` — терминальная операция, запускающая поток; тоже suspend.
- Отмена потока = отмена корутины-коллектора, дополнительного API не требуется.

## Q2. Чем Cold Flow отличается от Hot Flow?

Главное отличие — **кто и когда запускает производителя данных**. Cold flow ленив: код стартует при подписке и работает персонально под каждого коллектора. Hot flow активен: эмитит независимо от того, есть подписчики или нет, и все коллекторы делят один и тот же источник.

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

Практическое следствие: cold flow подходит для **запросов** (каждая подписка = новый сетевой вызов или чтение), hot flow — для **общего состояния и событий**, которые существуют независимо от UI (текущий статус, поток уведомлений). У cold flow поздний подписчик ничего не пропускает — он получает весь поток с начала; у hot flow поздний подписчик видит только то, что было после подписки (плюс `replay`-буфер, если задан).

## Q3. Какие основные операторы Flow вы знаете?

Операторы Flow делятся на **промежуточные** (intermediate) — возвращают новый Flow и ничего не запускают (`map`, `filter`, `zip`...), и **терминальные** — запускают сбор и возвращают результат (`collect`, `toList`, `first`, `reduce`). Промежуточные ленивы: они лишь описывают преобразование, реальная работа начинается с терминального оператора. Удобно группировать их по назначению:

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

## Q4. Что такое StateFlow и когда его использовать?

`StateFlow` — **hot flow**, который хранит ровно одно «текущее» значение и отдаёт его каждому новому подписчику немедленно. Это наблюдаемое состояние: что бы ни произошло, у потока всегда есть актуальное значение, доступное синхронно через `.value`.

**Когда использовать:** для хранения и наблюдения состояния экрана/компонента — список заказов, флаг загрузки, выбранный фильтр. Классический сценарий — экспонировать состояние из `ViewModel` через неизменяемый `StateFlow`, а внутри мутировать приватный `MutableStateFlow`.

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

**Особенности и подводные камни:**
- Всегда имеет начальное значение — поэтому подписчик никогда не «висит» в ожидании первого элемента, как у `SharedFlow`.
- **Конфляция по равенству:** новое значение публикуется, только если оно `!=` текущему (сравнение через `equals`). Промежуточные значения между подписками теряются, а повторная установка того же значения коллекторов не разбудит. Из-за этого `StateFlow` не годится для передачи событий: два одинаковых события подряд схлопнутся в одно.
- Концептуально это аналог Android `LiveData`, но без привязки к жизненному циклу (за безопасный сбор отвечает сам коллектор — см. `repeatOnLifecycle`).

## Q5. Что такое SharedFlow и чем отличается от StateFlow?

`SharedFlow` — более общий hot flow: без обязательного начального значения, с настраиваемым буфером воспроизведения (`replay`) и без конфляции по равенству. `StateFlow` фактически является частным случаем `SharedFlow` с `replay = 1` и схлопыванием дубликатов.

Поскольку `SharedFlow` не схлопывает одинаковые значения и может вообще ничего не хранить (`replay = 0`), он предназначен для **событий** — вещей, которые произошли один раз и должны быть доставлены каждому активному подписчику ровно столько раз, сколько случились.

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

**Эмпирическое правило:** `StateFlow` — для **состояния** (список, статус загрузки, текущий выбор), где важно одно актуальное значение. `SharedFlow` — для **one-shot событий** (навигация, toast, snackbar), где каждое событие должно отработать ровно один раз и не пережить пересоздание экрана.

**Подводный камень эмиссии:** при `replay = 0` событие, отправленное в момент, когда подписчиков нет, теряется. Если нужно гарантировать доставку, добавляют `extraBufferCapacity` и стратегию `onBufferOverflow`, либо используют `replay = 1`.

## Q6. Как обрабатывать ошибки в Flow?

Flow придерживается принципа **прозрачности исключений** (exception transparency): обрабатывать ошибки нужно декларативными операторами, а не `try/catch` вокруг `emit`. Ключевое свойство — оператор `catch` ловит исключения только из **upstream** (то, что выше по цепочке), но не из самого блока `collect`. Это разделяет ошибки производителя и ошибки потребителя.

Основные инструменты:
- `catch { }` — перехват upstream-исключений, можно отдать fallback через `emit`.
- `onCompletion { cause -> }` — финальный хук, срабатывает и при успехе, и при ошибке (аналог `finally`).
- `retry` / `retryWhen` — повтор подписки при ошибке, в том числе с задержкой и backoff.

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

## Q7. Как работает backpressure в Kotlin Flow?

В Kotlin Flow backpressure встроен «бесплатно» за счёт suspend-функций: `emit` — это suspend-вызов, поэтому производитель **физически приостанавливается**, пока коллектор не обработает предыдущий элемент. Очередь не растёт, потому что быстрый производитель просто ждёт медленного потребителя. Это отличает Flow от RxJava, где backpressure пришлось вводить отдельным типом `Flowable`.

Когда такое жёсткое связывание не нужно, поведение настраивают операторами:

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

**Как выбрать стратегию:**
- `buffer(N)` — когда нужна максимальная пропускная способность и потеря элементов недопустима: производитель и потребитель работают параллельно, элементы копятся в буфере.
- `conflate()` — когда важно только **последнее** значение и промежуточные можно отбросить (прогресс загрузки, координаты курсора). Это `buffer` с `onBufferOverflow = DROP_OLDEST` и ёмкостью 1.
- `collectLatest { }` — когда обработку предыдущего элемента нужно **прервать** при появлении нового (живой поиск, перерисовка по последнему состоянию).

## Q8. Что такое channelFlow и callbackFlow?

Обычный `flow { }` запрещает эмитить из другой корутины: `emit` должен вызываться строго в том же контексте, иначе будет ошибка нарушения контекста. `channelFlow` и `callbackFlow` снимают это ограничение — внутри они дают канал, в который можно безопасно писать из нескольких корутин и из callback-ов через `send`/`trySend`.

- **`channelFlow`** — когда поток собирается из нескольких параллельных источников (несколько корутин пишут в один поток). Можно запускать вложенные `launch` и слать элементы конкурентно.
- **`callbackFlow`** — частный случай для **моста между callback-API и Flow**: подписываемся на listener, шлём его события через `trySend`, а в `awaitClose` отписываемся при отмене коллектора. `awaitClose` обязателен — без него поток завершится сразу и листенер не успеет отработать (а ресурс утечёт).

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

## Q9. Как тестировать Kotlin Flow?

Тесты Flow всегда запускают в `runTest` — это тестовый билдер корутин, который подменяет реальные `delay` на виртуальное время, поэтому тесты не «спят» по-настоящему. Дальше выбирают подход по типу потока:

- **Cold flow** проще всего собрать терминально: `flow.toList()` и проверить содержимое.
- **Hot flow** (`StateFlow`/`SharedFlow`) собирать через `toList()` нельзя — он никогда не завершится. Для них используют библиотеку **Turbine**: `flow.test { awaitItem() ... }` подписывается на поток, по одному вытягивает эмиссии и в конце требует явно завершить подписку (`cancelAndIgnoreRemainingEvents`), иначе тест упадёт на «непрочитанных» событиях.
- Для асинхронных обновлений состояния прогоняют планировщик через `advanceUntilIdle()`.

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

## Q10. Чем Flow отличается от RxJava Observable?

Концептуально оба — реактивные потоки данных, но Flow построен на корутинах, а RxJava — на собственном рантайме и интерфейсах `Disposable`/`Scheduler`. Главная разница в эргономике: в Flow асинхронность выражается обычным suspend-кодом (без callback-цепочек), отмена и контекст наследуются от корутин, а backpressure встроен без отдельного типа.

| Критерий | Kotlin Flow | RxJava Observable |
|----------|-------------|-------------------|
| Язык | Kotlin (coroutines) | Java/Kotlin |
| Отмена | `cancel()` корутины | `Disposable.dispose()` |
| Контекст | `flowOn(Dispatcher)` | `observeOn()` / `subscribeOn()` |
| Backpressure | Встроен (suspend) | `Flowable` vs `Observable` |
| Hot stream | `StateFlow`/`SharedFlow` | `Subject` / `ConnectableObservable` |
| Размер библиотеки | ~100KB | ~3MB |
| Тестирование | `Turbine` + `runTest` | `TestObserver` |

**Что выбрать:** для нового Kotlin-кода предпочтительнее Flow — он легче (~100KB против ~3MB), интегрирован с языком и не тянет отдельную модель потоков. RxJava оправдан, если проект уже на нём построен или нужен его более широкий набор операторов «из коробки».

## Q11. Как использовать flowOn для смены контекста?

`flowOn(dispatcher)` переключает контекст выполнения для всех операторов **выше себя** по цепочке (upstream) — но не трогает то, что ниже, и не меняет контекст `collect`. Это решает типичную задачу: тяжёлую работу (IO, парсинг) увести с главного потока, оставив сбор и обновление UI на нём.

Важная деталь: контекст коллектора задаётся снаружи, той корутиной, в которой вызван `collect`. `flowOn` не может его поменять — отсюда и направление «только вверх». При нескольких `flowOn` каждый влияет на участок до следующего `flowOn` выше.

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

Под капотом `flowOn` создаёт внутренний канал между двумя частями пайплайна, поэтому upstream и downstream фактически выполняются в разных корутинах и могут работать параллельно. Отличие от `withContext`: `withContext` меняет контекст для одного блока императивного кода, а `flowOn` — декларативно для всего upstream-участка потока.

## Q12. Что такое flatMapLatest, flatMapMerge, flatMapConcat?

Это три варианта «плоского» разворачивания: каждый элемент исходного потока превращается во вложенный Flow, а результаты сливаются в один. Различаются они **тем, как обрабатываются перекрывающиеся вложенные потоки**, когда новый элемент пришёл раньше, чем завершился предыдущий вложенный поток:

- **`flatMapLatest`** — отменяет предыдущий вложенный поток при появлении нового элемента. Нужен «только последний» результат.
- **`flatMapMerge`** — запускает вложенные потоки параллельно и сливает их по мере готовности (по умолчанию до 16 одновременно). Порядок результатов не гарантирован.
- **`flatMapConcat`** — строго последовательный: следующий вложенный поток стартует только после полного завершения предыдущего. Порядок сохраняется.

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

## Q13. Как интегрировать Kotlin Flow со Spring WebFlux?

Spring WebFlux понимает `Flow` нативно: контроллер может вернуть `Flow<T>`, и фреймворк сам адаптирует его к реактивному `Publisher` (мосту с Reactor). Suspend-функция в контроллере соответствует `Mono` (одно значение), а `Flow` — `Flux` (поток значений). Никаких ручных конвертаций не нужно.

Это даёт два типичных применения: вернуть коллекцию как стрим и организовать **server-sent events** через `produces = TEXT_EVENT_STREAM_VALUE` — каждый элемент Flow уходит клиенту отдельным событием.

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

## Q14. Как избежать memory leak при использовании SharedFlow?

Корень проблемы: подписка на hot flow живёт ровно столько, сколько живёт собирающая её корутина. Если корутину запустить в scope, который переживает компонент (например `GlobalScope`), коллектор будет держать ссылку на `Activity`/`ViewModel` и не даст их собрать сборщику мусора — это и есть утечка. `SharedFlow`/`StateFlow` не завершаются сами, поэтому коллектор никогда не остановится «по своей воле».

**Решение — привязать сбор к жизненному циклу владельца:**
- собирать в scope, который сам отменяется (`viewModelScope`, `lifecycleScope`), а не в `GlobalScope`;
- на Android оборачивать сбор в `repeatOnLifecycle(STARTED)` — он автоматически отменяет подписку, когда экран уходит в фон, и пересоздаёт при возврате;
- либо хранить `Job` и явно отменять его в нужном callback (`onStop`).

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

## Q15. Что произойдёт при исключении внутри flow { } без catch?

Исключение **прекращает поток и распространяется вниз до коллектора** как обычное исключение корутины. Уже отправленные элементы дойдут, но всё после `throw` не выполнится, и `collect` завершится броском — поймать можно обычным `try/catch` вокруг `collect`.

Тонкость, которую часто проверяют на собеседовании: оператор `catch` ловит только **upstream**-исключения (из производителя и операторов выше). Исключение, брошенное **внутри блока `collect`**, считается ошибкой потребителя и оператором `catch` не перехватывается — это прямое следствие прозрачности исключений. Поэтому ошибки обработки в `collect` всегда оборачивают отдельным `try/catch`.

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

## Q16. Как работают zip и combine?

Оба объединяют два потока в один, но по разной логике срабатывания:

- **`zip`** работает **попарно**: ждёт по одному элементу из каждого потока, объединяет их в пару и движется дальше. Если потоки разной длины, результат завершается, как только закончится более короткий — лишние элементы длинного потока отбрасываются. Подходит, когда элементы строго соответствуют друг другу по позиции.
- **`combine`** реагирует на **любое изменение** в любом из потоков и комбинирует последние известные значения каждого. Первый результат появляется, только когда оба потока выдали хотя бы по одному значению; дальше каждый новый элемент любого потока пересчитывает результат. Подходит для комбинирования независимых состояний (например, фильтр + поисковый запрос).

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

## Q17. Что такое scan и runningFold?

`scan` и `runningFold` (это синонимы) — накапливающие операторы. Логика та же, что у `fold`/`reduce`: есть начальное значение-аккумулятор и функция, объединяющая аккумулятор с очередным элементом. Разница в том, что `reduce` — терминальный и отдаёт **только финальный** результат, а `scan` — промежуточный и **эмитит аккумулятор после каждого шага**, включая начальное значение.

Из-за этого `scan` удобен там, где нужна не итоговая сумма, а вся **история накопления**: бегущий итог, прогресс, последовательность состояний.

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

- [Kotlin Coroutines](kotlin-coroutines-interview.md) — suspend функции, CoroutineScope, Job, Dispatcher
- [Kotlin](kotlin-interview.md) — основы языка, null safety, data classes
- [Spring WebFlux](../../frameworks/spring/spring-webflux-interview.md) — реактивный стек Spring, Mono/Flux vs Flow
- [Reactive Streams](../../reactive/reactive-streams-interview.md) — спецификация backpressure (Publisher/Subscriber/Subscription)
- [Project Reactor](../../reactive/project-reactor-interview.md) — Reactor (Java): Mono, Flux — аналог Flow
- [RxJava](../../reactive/rxjava-interview.md) — сравнение Flow vs Observable/Flowable
- [Kotlin Serialization](kotlin-serialization-interview.md) — сериализация данных в Flow pipeline
- [Spring R2DBC](../../frameworks/spring/spring-r2dbc-interview.md) — реактивная работа с БД, возвращает Flow
