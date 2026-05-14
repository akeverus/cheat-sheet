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
> - [ ] `Flow` запускается сразу при объявлении и буферизует элементы до первого `collect()` | Это поведение `Channel` или hot-stream, а не `flow { }`. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает кеш «уже эмитнутых» значений, ловит `IllegalStateException` про suspend вне корутины при первом prod-deploy.
> - [x] `Flow` — холодный suspend-источник: код в `flow { }` стартует при `collect()` и каждый коллектор получает собственное выполнение | `emit()` вызывается из suspend-блока коллектора, поэтому backpressure встроен в saspend-машину корутин. ✓ ПРИМЕНЯТЬ: Spring WebFlux + R2DBC возвращает `Flow<Order>`, который материализуется только при подписке через `CoroutineCrudRepository`. 📋 ПРАВИЛО: «Cold Flow — рецепт, не блюдо». 🔗 См. Q2, Q13.
> - [ ] `Flow.collect()` запускает эмиссию в отдельном потоке без участия корутины-коллектора | `collect` — обычная suspend-функция, она исполняется в текущем `CoroutineContext` без скрытых потоков. ❌ ПОСЛЕДСТВИЕ: попытка вызвать `flow.collect { }` в обычной Java-функции даёт «Suspension functions can be called only within coroutine body», падает review.
> - [ ] `Flow` хранит все эмитнутые элементы и переотправляет их новым коллекторам | Это семантика `SharedFlow(replay = N)`, у обычного `flow { }` буфера нет. ❌ ПОСЛЕДСТВИЕ: late subscriber в чате не видит history; пользователь жалуется «открыл вторую вкладку — пусто», в логах нет ошибки.

## Q2. Чем Cold Flow отличается от Hot Flow?

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
> - [ ] Cold Flow одинаков для всех подписчиков, Hot Flow создаёт новую копию для каждого | Поменяны местами: cold пересоздаёт исполнение per-collector, hot шарит общий source. ❌ ПОСЛЕДСТВИЕ: команда выбирает `flow { fetchPage() }` для общего кеша, каждый клиент бьёт API заново — счёт за внешний сервис вырастает в 10×.
> - [ ] Cold Flow буферизует эмиссии в `Channel`, Hot Flow эмитит синхронно без буфера | Буферизация определяется `buffer()/conflate()`, а не cold/hot. ❌ ПОСЛЕДСТВИЕ: разработчик не добавляет `buffer()` к медленному `collect`, продюсер блокируется на каждом emit, throughput падает в 5 раз.
> - [ ] Cold и Hot Flow различаются только наличием `replay`, всё остальное идентично | `replay` — параметр `SharedFlow`; cold/hot отличаются жизненным циклом и количеством producer-runs. ❌ ПОСЛЕДСТВИЕ: junior подменяет `MutableStateFlow` на `flow { }` «для упрощения», UI перестаёт получать актуальное состояние при ротации экрана.
> - [x] Cold Flow перезапускает producer на каждый `collect()` и завершается сам, Hot Flow живёт независимо и шарит эмиссии между подписчиками | `flow { }` — cold (per-collector run); `StateFlow`/`SharedFlow` — hot (один producer, multicast late/current subscribers). ✓ ПРИМЕНЯТЬ: Android-ViewModel держит `MutableStateFlow<UiState>` для шаринга состояния между фрагментами, а repository отдаёт cold `flow { fetchPage() }` для on-demand загрузки. 📋 ПРАВИЛО: «Cold = run-per-collector, Hot = run-once-broadcast». 🔗 См. Q1, Q4, Q5.

## Q3. Какие основные операторы Flow вы знаете?

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
> - [ ] `flow.toList()` — промежуточный оператор, добавляет преобразование, но не запускает collect | `toList()` — терминальный, он сам вызывает `collect` и блокирует suspend до завершения. ❌ ПОСЛЕДСТВИЕ: разработчик пишет `flow.toList().map { }` ожидая lazy chain, при сценарии «бесконечный SSE-поток» сервис висит, наружу 504 timeout.
> - [ ] `zip` и `combine` — синонимы: оба ждут пары и пересчитывают на любом emit | `zip` ждёт ровно по одному элементу из каждого, `combine` пересчитывает на каждое новое значение любого из потоков. ❌ ПОСЛЕДСТВИЕ: dashboard отрисовывает «orders × users» через `zip`, после рестарта users-flow завершается раньше — orders молча перестают показываться.
> - [x] `map`/`filter` — промежуточные (cold, lazy), `collect`/`toList`/`first`/`reduce` — терминальные, запускающие выполнение | Промежуточные возвращают новый `Flow`, терминальные суспендят и вытягивают элементы; без терминального оператора ничего не выполняется. ✓ ПРИМЕНЯТЬ: WebFlux-контроллер возвращает `Flow<OrderDto>` без терминального оператора — Spring сам подписывается через адаптер `kotlinx-coroutines-reactor`. 📋 ПРАВИЛО: «Без terminal — Flow спит». 🔗 См. Q1, Q9.
> - [ ] `flow.distinct()` сравнивает элементы по идентичности (`===`) и кешируется на JVM heap навсегда | `distinctUntilChanged` сравнивает только с предыдущим элементом по `equals`, без полного кеша. ❌ ПОСЛЕДСТВИЕ: попытка дедуплицировать сенсорный поток через ожидание глобального `distinct()` ведёт к OOM на устройствах после нескольких часов работы.

## Q4. Что такое StateFlow и когда его использовать?

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
> - [ ] `StateFlow` доставляет каждое значение каждому коллектору, дубликаты не отбрасываются | `StateFlow` использует conflation по `equals`: повторный `emit` того же значения пропускается. ❌ ПОСЛЕДСТВИЕ: команда строит counter `loadingEvents` через `StateFlow<Int>`, два одинаковых события подряд теряются — метрики времени загрузки отчётливо ниже реальных.
> - [x] `StateFlow` — hot flow с обязательным начальным значением и conflation по `equals`, идеален для UI-state | Новый коллектор сразу получает текущее `value`; идентичные эмиссии пропускаются. ✓ ПРИМЕНЯТЬ: Android-ViewModel выставляет `val uiState: StateFlow<UiState>`, Compose-экран подписывается через `collectAsStateWithLifecycle()` и не перерисовывается при идентичных update. 📋 ПРАВИЛО: «StateFlow = текущее value + skip equal». 🔗 См. Q5, Q14.
> - [ ] `StateFlow` хранит историю всех значений и переотправляет её новым подписчикам | История хранится только последняя (replay = 1), полную историю даёт `SharedFlow(replay = N)`. ❌ ПОСЛЕДСТВИЕ: разработчик ожидает audit-trail из `StateFlow`, аналитика теряет промежуточные состояния заказа, post-mortem невозможен.
> - [ ] `StateFlow` создаётся без начального значения и эмитит только пользовательские события | Нельзя сконструировать `MutableStateFlow` без initial value — компилятор требует параметр. ❌ ПОСЛЕДСТВИЕ: попытка применить `StateFlow` для one-shot navigation events ведёт к показу первого события каждый раз при открытии экрана (toast «Заказ создан» при ротации).

## Q5. Что такое SharedFlow и чем отличается от StateFlow?

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
> - [ ] `SharedFlow(replay = 0)` гарантирует доставку всех событий late subscriber'ам | `replay = 0` означает, что новый подписчик НЕ увидит прошлые события вообще. ❌ ПОСЛЕДСТВИЕ: чат-фронт переподключается после network blip, последние 5 сообщений не приходят — пользователь видит «дыру» в timeline.
> - [ ] `SharedFlow` всегда требует начального значения, как и `StateFlow` | `SharedFlow` создаётся без initial value, в этом и отличие от `StateFlow`. ❌ ПОСЛЕДСТВИЕ: разработчик копирует `MutableSharedFlow(emptyList())` из туториала, не компилируется, тратит час на отладку перед review.
> - [x] `SharedFlow` — hot flow с настраиваемым `replay` и `extraBufferCapacity`, без начального значения; `StateFlow` — частный случай `SharedFlow(replay = 1, conflate)` для state | Использование: state → `StateFlow`, one-shot events → `SharedFlow(replay = 0)`. ✓ ПРИМЕНЯТЬ: ViewModel в Wolt-app шлёт `OrderEvent` через `MutableSharedFlow(replay = 0, extraBufferCapacity = 16, onBufferOverflow = DROP_OLDEST)` — toast «Доставка началась» показывается один раз. 📋 ПРАВИЛО: «State → StateFlow, Event → SharedFlow». 🔗 См. Q4, Q14.
> - [ ] Разница только в имени: `SharedFlow` и `StateFlow` идентичны по семантике | У них разный contract: equality-conflation, replay, обязательность initial value. ❌ ПОСЛЕДСТВИЕ: миграция UI с `StateFlow` на `SharedFlow(replay = 1)` без conflation удваивает рекомпозиции в Compose, p95 frame time прыгает с 8 ms до 24 ms.

## Q6. Как обрабатывать ошибки в Flow?

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
> - [ ] `try/catch` вокруг `flow { emit() }` ловит ошибки upstream корректно и отменяет downstream | Бросать или ловить исключения внутри `flow {}` запрещено `exception transparency`; исключение, попавшее в `try` в emitter, не пройдёт по downstream правильно. ❌ ПОСЛЕДСТВИЕ: pipeline тихо «съедает» exception, retry-логика не срабатывает, операция висит в consumer-lag, пока monitor не заметит через час.
> - [ ] `catch { }` после `collect { }` перехватывает исключения внутри блока коллектора | `catch` ловит ТОЛЬКО upstream-исключения; ошибки в самом `collect` блоке не перехватываются. ❌ ПОСЛЕДСТВИЕ: разработчик полагается на `catch` для NPE в `updateUI()`, краш всё равно прилетает в Crashlytics, а тест зелёный — потому что unit-test упирается в `runTest`.
> - [x] `catch { }` ставится перед `collect` и ловит upstream-исключения; `retry`/`retryWhen` повторяют сборку при ошибке; `onCompletion` вызывается всегда | Разделение upstream/downstream — основа exception-transparency. `retryWhen` поддерживает backoff. ✓ ПРИМЕНЯТЬ: Spring Cloud Gateway-фильтр оборачивает downstream-вызов через `flow.retryWhen { e, n -> e is IOException && n < 3 }.catch { emit(fallback) }`, давая 3 retries с экспоненциальным backoff. 📋 ПРАВИЛО: «catch перед collect — ловит upstream». 🔗 См. Q15.
> - [ ] `flow.catch { e -> throw e }` корректно «ремитит» ошибку дальше и эквивалентен отсутствию `catch` | Внутри `catch` нельзя `throw` чужое exception — нарушается exception-transparency и завершение flow становится некорректным. ❌ ПОСЛЕДСТВИЕ: после миграции на coroutines 1.7 поведение меняется, тесты падают, post-mortem на 4 часа отладки в legacy-кодовой базе.

## Q7. Как работает backpressure в Kotlin Flow?

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
> - [ ] `buffer()`, `conflate()` и `collectLatest` — синонимы, выбор любого даёт идентичный результат | `buffer` — параллелизм с очередью, `conflate` — drop-old без обработки, `collectLatest` — отмена обработки при новом элементе. ❌ ПОСЛЕДСТВИЕ: команда меняет `buffer(Channel.UNLIMITED)` на `conflate()` «для упрощения», теряет 70% событий аналитики, метрики не сходятся с фронтом.
> - [x] `buffer()` запускает producer и consumer параллельно с очередью; `conflate()` оставляет только последнее значение; `collectLatest` отменяет обработку при новом элементе | Каждая стратегия решает свою задачу: throughput, latest-only state, cancel-and-restart. ✓ ПРИМЕНЯТЬ: Android-поиск использует `searchFlow.debounce(300).flatMapLatest { api.search(it) }.collectLatest { renderResults(it) }` — отменяет старый запрос при новом вводе. 📋 ПРАВИЛО: «buffer = всё, conflate = последнее, collectLatest = отмени-и-перезапусти». 🔗 См. Q12.
> - [ ] `Flow` не поддерживает backpressure: producer всегда быстрее consumer и переполняет heap | По умолчанию producer suspend'ится в `emit` пока collector не готов — это и есть встроенный backpressure без отдельной API. ❌ ПОСЛЕДСТВИЕ: разработчик добавляет «защитный» `Channel(UNLIMITED)` поверх `flow`, на проде heap уходит в OOM при медленном downstream через 30 минут.
> - [ ] `collectLatest` ждёт завершения текущей обработки и буферизует все промежуточные элементы | `collectLatest` именно отменяет текущий блок-обработчик при следующем элементе, не буферизует. ❌ ПОСЛЕДСТВИЕ: команда строит «processing pipeline» через `collectLatest`, при росте RPS теряет 60% входных событий, фикс — переход на `buffer().collect`.

## Q8. Что такое channelFlow и callbackFlow?

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
> - [ ] `flow { }` поддерживает `send()` из нескольких корутин, `channelFlow` — нет | Ровно наоборот: в `flow {}` `emit` запрещён вне корутины-сборщика, в `channelFlow` `send` доступен из любой launched корутины. ❌ ПОСЛЕДСТВИЕ: junior пишет fan-in через `flow {}` + `launch`, получает `IllegalStateException: Flow invariant is violated` при первом запуске тестов.
> - [x] `channelFlow` поддерживает конкурентный `send` из дочерних корутин; `callbackFlow` — специализация для оборачивания callback-API с обязательным `awaitClose { }` | Внутренне используют `Channel`, поэтому emit потокобезопасен. `awaitClose` нужен для cleanup listener'а при отмене коллектора. ✓ ПРИМЕНЯТЬ: Android-обёртка `LocationManager` через `callbackFlow { val cb = ...; addListener(cb); awaitClose { removeListener(cb) } }` гарантирует отписку GPS при остановке экрана. 📋 ПРАВИЛО: «callbackFlow обязан awaitClose». 🔗 См. Q14.
> - [ ] В `callbackFlow` достаточно вызвать `close()` и `awaitClose` не нужен — отписка произойдёт автоматически | Без `awaitClose` корутина внутри `callbackFlow` завершится сразу после регистрации listener'а, и cleanup при отмене коллектора не выполнится. ❌ ПОСЛЕДСТВИЕ: WebSocket-клиент не получает `unregister`, держит references на view, утечка памяти 50 MB/час, OOM в Crashlytics через сутки.
> - [ ] `channelFlow` создаёт unbounded очередь по умолчанию и не имеет backpressure | По умолчанию ёмкость канала RENDEZVOUS (0), `send` суспендится — backpressure встроен. ❌ ПОСЛЕДСТВИЕ: команда полагается на «безопасную» очередь и не ставит `Channel.BUFFERED`, при burst-нагрузке упирается в неожиданное замедление producer'а, post-mortem за день.

## Q9. Как тестировать Kotlin Flow?

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
> - [ ] `Thread.sleep(1000)` внутри `runTest` корректно ускоряется виртуальным временем | `runTest` ускоряет только корутинные `delay`, `Thread.sleep` всё ещё блокирует реальный поток. ❌ ПОСЛЕДСТВИЕ: `flow.debounce(5_000)` тестируется через `Thread.sleep`, прогон unit-тестов раздувается до 30 минут на CI, разработчики выключают локальные runs.
> - [ ] `Turbine.test { }` достаточно вызывать без `awaitItem`/`awaitComplete` — он сам зачищает остаток | Turbine падает с `Expected complete or X items but received Y` если ожидания не явные. ❌ ПОСЛЕДСТВИЕ: тест зелёный локально, флакающий в CI — потому что emission приходит после assertion'а в зависимости от scheduler'а.
> - [x] `runTest { }` + `Turbine.test { }` обеспечивают виртуальное время, контроль `awaitItem`/`awaitComplete` и явный `cancelAndIgnoreRemainingEvents` | `runTest` использует `TestCoroutineScheduler` для virtual time; Turbine форсит явные ожидания, исключая race'ы. ✓ ПРИМЕНЯТЬ: команда Cash App тестирует все Flow-pipelines через `Turbine` (это их собственная библиотека), CI прогоняет 5K тестов за 90 секунд. 📋 ПРАВИЛО: «runTest для virtual time, Turbine для явных await». 🔗 См. Q1, Q12.
> - [ ] Тестирование `StateFlow` требует обязательного `Dispatchers.Main` через `Robolectric` | `StateFlow` тестируется в `runTest` без любых Android-зависимостей; `Dispatchers.Main` нужен только Android-UI коду. ❌ ПОСЛЕДСТВИЕ: домен-модуль тащит Robolectric в classpath «для тестов StateFlow», build time прыгает с 2 до 8 минут, CI-стоимость растёт.

## Q10. Чем Flow отличается от RxJava Observable?

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
> - [ ] `Flow` и `Observable` идентичны: одинаковая семантика, операторы, lifecycle | У них разные exception-transparency rules, отмена через корутины vs `Disposable`, встроенный backpressure (Flow) vs опциональный (`Flowable`). ❌ ПОСЛЕДСТВИЕ: миграция «один-в-один» из RxJava в Flow ломает обработку ошибок — `onErrorResumeNext` логика не воспроизводится через `catch`, post-mortem на 2 дня.
> - [x] `Flow` встроен в корутины, отменяется через `cancel()` корутины, имеет встроенный backpressure через suspend; `Observable` — отдельная JVM-библиотека с `Disposable` и без backpressure (для backpressure нужен `Flowable`) | Размер kotlinx.coroutines ~100 KB против ~3 MB RxJava; экосистема Spring/Android идёт в сторону Flow. ✓ ПРИМЕНЯТЬ: новые сервисы Spring WebFlux на Kotlin используют `Flow<T>` вместо `Flux<T>` — Spring адаптирует автоматически через `kotlinx-coroutines-reactor`. 📋 ПРАВИЛО: «Flow = coroutines-native, Rx = standalone». 🔗 См. Q7, Q13.
> - [ ] `RxJava` Observable работает только в Java, в Kotlin не используется | RxJava полностью совместим с Kotlin и активно использовался до релиза `kotlinx.coroutines.flow` 1.3 (2019). ❌ ПОСЛЕДСТВИЕ: новый разработчик не знает RxJava-историю проекта, ломает `Observable.create` в legacy-Android приложении при попытке «перевести на Kotlin».
> - [ ] У `Flow` нет аналога `BehaviorSubject`/`PublishSubject`, нужно писать свой | `StateFlow` ≈ `BehaviorSubject` (текущее value), `SharedFlow(replay = 0)` ≈ `PublishSubject` (without replay). ❌ ПОСЛЕДСТВИЕ: разработчик тащит зависимость на RxJava ради `BehaviorSubject` в новый проект, увеличивает APK на 3 MB и duplicate-тесты на оба stream-API.

## Q11. Как использовать flowOn для смены контекста?

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
> - [ ] `flowOn(Dispatchers.IO)` меняет контекст для всех downstream операторов и `collect` | `flowOn` влияет ТОЛЬКО на upstream (то, что объявлено выше по цепочке). ❌ ПОСЛЕДСТВИЕ: `flow.flowOn(IO).collect { updateUI(it) }` запускает `updateUI` всё ещё в IO-пуле, в Android — `CalledFromWrongThreadException`, в Spring — лишний context switch.
> - [ ] `flowOn` эквивалентен `withContext` внутри `flow {}` | `flowOn` создаёт внутренний канал между двумя частями pipeline'а; `withContext` суспендит и переключает контекст текущего блока. ❌ ПОСЛЕДСТВИЕ: попытка `flow { withContext(IO) { emit(x) } }` бросает `IllegalStateException: Flow invariant is violated regarding context preservation`.
> - [x] `flowOn(Dispatcher)` переключает контекст для UPSTREAM операторов через внутренний канал; downstream остаётся в контексте коллектора | Несколько `flowOn` создают сегменты с разными dispatcher'ами; правило размещения — ближе к источнику. ✓ ПРИМЕНЯТЬ: репозиторий читает файл и парсит JSON: `flow { emit(readFile()) }.flowOn(IO).map { parse(it) }.flowOn(Default).collect { showUi(it) }` — IO для диска, Default для CPU, Main для UI. 📋 ПРАВИЛО: «flowOn — для всего ВЫШЕ». 🔗 См. Q1, Q13.
> - [ ] Несколько вызовов `flowOn` в одной цепочке всегда переопределяют друг друга, последний выигрывает | Каждый `flowOn` действует только на сегмент upstream до следующего `flowOn`; они НЕ переопределяют, а накладываются по сегментам. ❌ ПОСЛЕДСТВИЕ: команда «упрощает» pipeline до одного `flowOn(IO)` в конце, CPU-bound JSON-парсинг забивает IO-пул, p99 latency прыгает с 50 ms до 2 s под нагрузкой.

## Q12. Что такое flatMapLatest, flatMapMerge, flatMapConcat?

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
> - [ ] `flatMapMerge` гарантирует порядок результатов в порядке исходных элементов | `flatMapMerge` запускает inner flows конкурентно, порядок результатов НЕ гарантирован — кто быстрее завершился, тот и приходит первым. ❌ ПОСЛЕДСТВИЕ: pipeline нумерует страницы pagination через `flatMapMerge`, отображение перемешивает страницы 5 → 2 → 7 → 1, пользователи жалуются «листание сломано».
> - [ ] `flatMapLatest` буферизует все промежуточные эмиссии и обрабатывает их последовательно | `flatMapLatest` отменяет inner flow при появлении нового элемента в outer flow. ❌ ПОСЛЕДСТВИЕ: команда применяет `flatMapLatest` для аналитики событий, теряет 90% событий между быстрыми эмиссиями, метрика DAU съезжает.
> - [x] `flatMapLatest` отменяет предыдущий inner flow при новом outer-элементе; `flatMapMerge(concurrency)` запускает до N параллельно; `flatMapConcat` ждёт завершения предыдущего | Применение: `Latest` — search-as-you-type, `Merge` — independent fan-out, `Concat` — sequential зависимости. ✓ ПРИМЕНЯТЬ: Booking.com search-bar — `query.debounce(300).flatMapLatest { searchApi(it) }`, отменяет старый запрос при печати новой буквы. 📋 ПРАВИЛО: «Latest = отмени старое, Merge = параллельно, Concat = в очередь». 🔗 См. Q8, Q12.
> - [ ] `flatMapConcat` и `flatMapMerge` — синонимы и взаимозаменяемы | `Concat` — строго последовательный (waiting), `Merge` — параллельный (concurrent), под капотом разные scheduling-стратегии. ❌ ПОСЛЕДСТВИЕ: разработчик переходит с `Concat` на `Merge` «для скорости», нарушает порядок DB-операций, foreign key violation в production.

## Q13. Как интегрировать Kotlin Flow со Spring WebFlux?

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
>
> **Вопрос:** Как Spring WebFlux обрабатывает `Flow<T>` от controller и где разница с `Flux<T>`?
>
> ---
>
> #### A) WebFlux конвертирует `Flow` в `Flux` через `ReactiveAdapterRegistry` и обрабатывает как обычный reactive Publisher — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> Spring WebFlux под капотом использует **Reactor** (`Flux`/`Mono` — реализация Reactive Streams). Kotlin `Flow` — другая абстракция (cold flow, suspend-based), но Spring предоставляет **bridge** через `kotlinx-coroutines-reactor` модуль: метод `.asFlux()` конвертирует Flow в Flux, `.asFlow()` обратно. `ReactiveAdapterRegistry` автоматически подхватывает Kotlin Flow type и применяет конверсию.
>
> С точки зрения разработчика разницы между `fun get(): Flow<T>` и `fun get(): Flux<T>` для контроллера почти нет — Spring обрабатывает оба одинаково. Различия — на уровне идиоматики: Flow более естественно в Kotlin codebase, `suspend fun` для одиночных значений (вместо `Mono`).
>
> **Пример:**
> ```kotlin
> @RestController
> @RequestMapping("/orders")
> class OrderController(private val service: OrderService) {
>     // Flow → Spring сам конвертирует в Flux при сериализации response
>     @GetMapping fun getOrders(): Flow<OrderDto> = service.streamOrders()
>
>     // SSE — Flow стримит события через event-stream
>     @GetMapping("/{id}/events", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
>     fun streamEvents(@PathVariable id: String): Flow<OrderEvent> =
>         service.getOrderEvents(id)
>
>     // suspend fun — эквивалент Mono<OrderDto>
>     @GetMapping("/{id}")
>     suspend fun getOrder(@PathVariable id: String): OrderDto =
>         service.findById(id) ?: throw NotFoundException()
> }
>
> // Spring Data R2DBC + coroutines — CoroutineCrudRepository
> interface OrderRepository : CoroutineCrudRepository<Order, Long> {
>     fun findByStatus(status: OrderStatus): Flow<Order>
>     suspend fun findById(id: Long): Order?
> }
> ```
>
> **Когда применять:**
> - **Kotlin-first WebFlux проекты** — Flow + suspend читается чище чем Mono/Flux/`.flatMap{ }`.
> - **Server-Sent Events / streaming endpoints** — Flow с backpressure через `kotlinx-coroutines-reactor`.
> - **Spring Data R2DBC** — `CoroutineCrudRepository` для коротких запросов; для аналитики — `DatabaseClient` через `awaitSingle()`/`flow`.
> - **Yandex/Wolt mobile API** — Kotlin Multiplatform клиент + Spring WebFlux backend с Flow.
>
> **Подводные камни:**
> - **Backpressure** в Flow — cooperative через `buffer()`, `conflate()`, `collectLatest`. В Flux — Reactor-style request/cancel. При конвертации Flow → Flux backpressure пробрасывается, но семантика может неожиданно отличаться (например, `collectLatest` ≠ `switchMap`).
> - **Dispatcher leak**: Flow по умолчанию работает на dispatcher вызывающего. В WebFlux endpoint это event-loop поток. Если в Flow есть блокирующая операция (JDBC) — нужен `.flowOn(Dispatchers.IO)`.
> - **`@PreAuthorize` + `suspend`**: работает, но требует `kotlin-reflect` и Spring Security ≥ 5.5; раньше нужны были workaround через `MonoSecurityContext`.
> - **OpenAPI generation** для Flow: Springdoc корректно понимает `Flow<T>` → `Flux<T>` начиная с v2.0; на старых версиях документация генерируется неверно.
>
> **Связанные вопросы:** [[Q1]] — определение Flow vs Sequence; [[Q5]] — Hot vs Cold flows и SharedFlow; [[Q14]] — memory leak при подписке на SharedFlow в Spring beans.
>
> ---
>
> #### B) WebFlux не поддерживает Kotlin Flow — нужно вручную конвертировать `.asPublisher()` — ❌ Неверно
>
> **Что на самом деле:** WebFlux поддерживает Flow **из коробки** (через `kotlinx-coroutines-reactor`, который автоматически подключается при наличии coroutines в classpath). Ручная конвертация `.asPublisher()` или `.asFlux()` не нужна — Spring справляется сам.
>
> **Откуда путаница:** в старых версиях Spring (5.0-5.2) поддержка корутин была ограниченной, и приходилось вручную писать `.asFlux()`. С 5.3+ это работает прозрачно.
>
> **Если бы это было правдой:** каждый controller с Flow требовал бы шаблонного `.asFlux()` в конце. На практике этот код пишется один раз в integration с библиотекой, не в коде приложения.
>
> ---
>
> #### C) `Flow<T>` блокирует event-loop в WebFlux — нужно использовать только `Flux<T>` — ❌ Неверно
>
> **Что на самом деле:** Kotlin Flow — **non-blocking** suspend-based абстракция. Под капотом Flow использует continuation passing style (CPS) — это та же модель что у Reactor, не блокирующая. Spring Reactor Netty event-loop не блокируется при использовании Flow.
>
> **Откуда путаница:** suspend functions «выглядят как» блокирующий код (`val x = repo.findById(id)`). Но это лишь синтаксический сахар над non-blocking continuation; компилятор Kotlin генерирует state machine.
>
> **Если бы это было правдой:** Kotlin/Spring экосистема была бы непригодна для high-load reactive API. На практике Yandex/Wolt/Avito используют Kotlin+WebFlux+Flow в production на тысячах RPS.
>
> ---
>
> #### D) `Flow` работает только с `R2dbcRepository`, не с `WebFlux` controller — ❌ Неверно
>
> **Что на самом деле:** Flow работает **везде в reactive Spring стеке**: controller, service, repository, WebClient, тесты. WebFlux принимает Flow в response, WebClient может возвращать Flow (`.bodyToFlow<T>()`), R2DBC репозитории возвращают Flow.
>
> **Откуда путаница:** Flow часто демонстрируют именно с R2DBC. Но это просто популярный use-case — Flow универсален.
>
> **Если бы это было правдой:** мы могли бы получать Flow от БД, но не возвращать его клиенту — пришлось бы конвертировать в Flux. На практике Flow → клиент идёт прозрачно.

## Q14. Как избежать memory leak при использовании SharedFlow?

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
>
> **Вопрос:** Почему `GlobalScope.launch` для коллектора `SharedFlow` приводит к memory leak в Android Activity?
>
> ---
>
> #### A) `GlobalScope` создаёт корутину которая работает быстрее чем lifecycleScope — это race condition — ❌ Неверно
>
> **Что на самом деле:** проблема не в скорости. `GlobalScope` создаёт корутину **с lifetime = время жизни приложения**, не привязанную к жизненному циклу Activity. Когда Activity уничтожается (`onDestroy`), корутина продолжает работать, удерживая ссылку на Activity через лямбду `handleEvent(it)`.
>
> **Откуда путаница:** memory leak интуитивно ассоциируется с многопоточностью и race conditions. На деле — про lifetime mismatch: subscriber переживает publisher's scope.
>
> **Если бы это было правдой:** проблема решалась бы добавлением `delay()` или `yield()` — задержки. На практике замедление не помогает; нужно остановить корутину при `onDestroy`.
>
> ---
>
> #### B) `SharedFlow` хранит strong reference на collector lambda; при бесконечной жизни scope лямбда (и её захваченные `this`) не освобождаются GC, удерживая Activity и весь его VM — ✓ Верно
>
> **Развёрнутое объяснение:**
>
> `SharedFlow` — это hot flow, который ведёт **внутренний список подписчиков** (`flow.subscribers`). Когда мы делаем `flow.collect { handleEvent(it) }`, лямбда регистрируется в этом списке. Лямбда захватывает `this` (Activity) через выражение `handleEvent`.
>
> Если корутина бежит в `GlobalScope`, она живёт до конца процесса. Соответственно, лямбда не удаляется из subscribers list, и сильная ссылка на Activity сохраняется. GC видит «Activity достижима через GlobalScope → flow.subscribers → lambda → this» и НЕ удаляет её.
>
> Результат: Activity, ViewBinding, ViewModel, drawable, bitmaps — всё остаётся в памяти после `onDestroy`. На каждом orientation change или re-creation — новый leak.
>
> **Пример (правильно vs неправильно):**
> ```kotlin
> // ❌ LEAK: GlobalScope живёт всю жизнь приложения
> class BadActivity : AppCompatActivity() {
>     override fun onCreate(savedInstanceState: Bundle?) {
>         super.onCreate(savedInstanceState)
>         GlobalScope.launch {
>             viewModel.events.collect { event ->         // лямбда → this → Activity → ViewModel...
>                 updateUi(event)
>             }
>         }
>     }
> }
>
> // ✅ ПРАВИЛЬНО: lifecycle-aware scope
> class GoodActivity : AppCompatActivity() {
>     override fun onCreate(savedInstanceState: Bundle?) {
>         super.onCreate(savedInstanceState)
>         lifecycleScope.launch {
>             repeatOnLifecycle(Lifecycle.State.STARTED) {
>                 viewModel.events.collect { event ->     // отменяется на onStop, рестартует на onStart
>                     updateUi(event)
>                 }
>             }
>         }
>     }
> }
>
> // ✅ Альтернатива: явный Job + cancel в onDestroy/onStop
> class AlternativeActivity : AppCompatActivity() {
>     private var collectJob: Job? = null
>     override fun onStart() {
>         super.onStart()
>         collectJob = lifecycleScope.launch {
>             viewModel.events.collect { updateUi(it) }
>         }
>     }
>     override fun onStop() {
>         super.onStop()
>         collectJob?.cancel()                            // явно убираем подписку
>     }
> }
> ```
>
> **Когда применять:**
> - **Android**: всегда `lifecycleScope.launch` + `repeatOnLifecycle` для UI-коллекторов. Это стандартный паттерн с Lifecycle 2.4+ (2021).
> - **Spring beans с SharedFlow**: используйте `@PreDestroy` для отмены корутин при остановке bean (`@Service` lifecycle).
> - **Compose**: `LaunchedEffect(key)` или `collectAsState()` — Compose сам управляет lifecycle.
> - **ViewModel**: `viewModelScope` — отменяется в `onCleared()` автоматически.
>
> **Подводные камни:**
> - **`StateFlow.collect` блокирует корутину навсегда** — даже без новых эмиссий, поскольку StateFlow никогда не завершается. Это by design, но удивляет начинающих.
> - **`repeatOnLifecycle` ≠ `flowWithLifecycle`**: первый рестартует collector при resume, второй пропускает значения когда состояние ниже минимального. Выбор зависит от сценария (UI vs background work).
> - **Multiple collectors на одной Flow** — каждый collect создаёт **отдельную subscribers entry** в SharedFlow. Параллельная подписка из 5 Activities = 5 lambda references.
> - **Leak detection**: LeakCanary видит коллекторы с retained Activity. Но первопричина — `GlobalScope`, а не SharedFlow per se.
>
> **Связанные вопросы:** [[Q5]] — Hot vs Cold flow и SharedFlow basics; [[Q6]] — StateFlow и conflation; [[Q15]] — exception handling при collect отменяет коллектор автоматически.
>
> ---
>
> #### C) `SharedFlow` всегда вызывает memory leak — лучше использовать `StateFlow` — ❌ Неверно
>
> **Что на самом деле:** memory leak зависит от scope сборки, не от типа flow. `StateFlow` имеет ту же проблему если коллектор в `GlobalScope`. Разница между StateFlow и SharedFlow — в semantics (conflated state vs broadcast events), не в безопасности по памяти.
>
> **Откуда путаница:** StateFlow «выглядит проще», и для UI обычно подходит лучше. Но утечка возникает из-за scope коллектора, не из-за выбора типа.
>
> **Если бы это было правдой:** мы бы могли использовать SharedFlow только для одноразовых событий через `consumeAsFlow()`. Но это бы заблокировало главное применение SharedFlow — event bus для multiple subscribers.
>
> ---
>
> #### D) `lifecycleScope` сам по себе достаточен; `repeatOnLifecycle` нужен только для производительности — ❌ Неверно
>
> **Что на самом деле:** `lifecycleScope.launch` без `repeatOnLifecycle` стартует одну корутину при `onCreate`. Эта корутина живёт до **уничтожения Activity** (`onDestroy`), что означает collect продолжается **даже когда Activity на фоне** (`onStop`). Это растрата ресурсов: обновления UI идут, когда пользователь не видит экран.
>
> `repeatOnLifecycle(STARTED)` отменяет корутину при `onStop` и пересоздаёт при `onStart` — экономит CPU/battery, что критично на mobile.
>
> **Откуда путаница:** `lifecycleScope` звучит как «полное решение». На деле он лишь обеспечивает cancel при destroy, но не оптимизирует время жизни между start/stop.
>
> **Если бы это было правдой:** background activities обрабатывали бы updates вхолостую. На Android 12+ это может приводить к ANR — система мониторит и убивает background workers.

## Q15. Что произойдёт при исключении внутри flow { } без catch?

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Как работают zip и combine? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. Что такое scan и runningFold? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Kotlin Coroutines](kotlin-coroutines-interview.md) — suspend функции, CoroutineScope, Job, Dispatcher ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Kotlin](kotlin-interview.md) — основы языка, null safety, data classes
- [Spring WebFlux](../../frameworks/spring/spring-webflux-interview.md) — реактивный стек Spring, Mono/Flux vs Flow
- [Reactive Streams](../../reactive/reactive-streams-interview.md) — спецификация backpressure (Publisher/Subscriber/Subscription)
- [Project Reactor](../../reactive/project-reactor-interview.md) — Reactor (Java): Mono, Flux — аналог Flow
- [RxJava](../../reactive/rxjava-interview.md) — сравнение Flow vs Observable/Flowable
- [Kotlin Serialization](kotlin-serialization-interview.md) — сериализация данных в Flow pipeline
- [Spring R2DBC](../../frameworks/spring/spring-r2dbc-interview.md) — реактивная работа с БД, возвращает Flow
