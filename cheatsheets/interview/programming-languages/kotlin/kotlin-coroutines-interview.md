---
title: "Вопросы на собеседовании: Kotlin Coroutines"
description: "Полное руководство по Kotlin Coroutines: structured concurrency, Flow, Channel, диспетчеры, обработка ошибок, тестирование и интеграция со Spring"
tags:
  - interview
  - programming-languages
  - kotlin-coroutines-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Kotlin Coroutines"
  - "Kotlin Coroutines interview"
  - "корутины Kotlin"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Kotlin Coroutines`

Полное руководство по `Kotlin Coroutines` для подготовки к собеседованию на позицию `Senior Kotlin/Java Developer`. Охватывает `structured concurrency`, `Flow`, `Channel`, диспетчеры, обработку ошибок, тестирование и интеграцию с `Spring`.

## Полезные ссылки

### Официальная документация

- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html) — официальный гайд по корутинам
- [Coroutines and Channels](https://kotlinlang.org/docs/channels.html) — документация по каналам
- [Kotlin Flow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) — API-документация Flow
- [Exception Handling](https://kotlinlang.org/docs/exception-handling.html) — обработка исключений в корутинах
- [Best practices for coroutines in Android](https://developer.android.com/kotlin/coroutines/coroutines-best-practices) — рекомендации Google

### Baeldung

- [Introduction to Kotlin Coroutines — Baeldung](https://www.baeldung.com/kotlin/coroutines) — введение в корутины: launch, async, suspend
- [Coroutine Context and Dispatchers — Baeldung](https://www.baeldung.com/kotlin/coroutine-context-dispatchers) — CoroutineContext и диспетчеры
- [Threads vs Coroutines in Kotlin — Baeldung](https://www.baeldung.com/kotlin/threads-coroutines) — сравнение потоков и корутин
- [How to Run Parallel Coroutines in Kotlin — Baeldung](https://www.baeldung.com/kotlin/parallel-coroutines) — параллельный запуск корутин

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы корутин**
- [Q1. (!) Что такое корутины и зачем они нужны?](#q1--что-такое-корутины-и-зачем-они-нужны)
- [Q2. (!) В чём разница между потоками и корутинами?](#q2--в-чём-разница-между-потоками-и-корутинами)
- [Q3. (!) Что такое `suspend`-функция и как она работает под капотом?](#q3--что-такое-suspend-функция-и-как-она-работает-под-капотом)
- [Q4. Как определить и запустить корутину?](#q4-как-определить-и-запустить-корутину)
- [Q5. (!) В чём разница между `launch` и `async`?](#q5--в-чём-разница-между-launch-и-async)
- [Q6. В чём разница между асинхронностью и параллелизмом?](#q6-в-чём-разница-между-асинхронностью-и-параллелизмом)

**Диспетчеры и контекст**
- [Q7. (!) Что такое `CoroutineContext` и из чего он состоит?](#q7--что-такое-coroutinecontext-и-из-чего-он-состоит)
- [Q8. (!) Какие `Dispatchers` существуют и когда какой использовать?](#q8--какие-dispatchers-существуют-и-когда-какой-использовать)
- [Q9. Что такое `withContext` и чем отличается от `async`?](#q9-что-такое-withcontext-и-чем-отличается-от-async)

**Structured Concurrency и Scope**
- [Q10. (!) Что такое `structured concurrency`?](#q10--что-такое-structured-concurrency)
- [Q11. (!) Что такое `CoroutineScope` и зачем он нужен?](#q11--что-такое-coroutinescope-и-зачем-он-нужен)
- [Q12. В чём разница между `GlobalScope` и scope с жизненным циклом?](#q12-в-чём-разница-между-globalscope-и-scope-с-жизненным-циклом)
- [Q13. Что такое `coroutineScope` (функция) и чем отличается от `CoroutineScope` (конструктор)?](#q13-что-такое-coroutinescope-функция-и-чем-отличается-от-coroutinescope-конструктор)
- [Q14. Что такое `runBlocking` и когда его использовать?](#q14-что-такое-runblocking-и-когда-его-использовать)

**Job, отмена и жизненный цикл**
- [Q15. (!) Что такое `Job` и каков его жизненный цикл?](#q15--что-такое-job-и-каков-его-жизненный-цикл)
- [Q16. Как отменить корутину и почему отмена кооперативна?](#q16-как-отменить-корутину-и-почему-отмена-кооперативна)
- [Q17. Что такое `yield` и `ensureActive`?](#q17-что-такое-yield-и-ensureactive)
- [Q18. Как обеспечить отмену при таймауте?](#q18-как-обеспечить-отмену-при-таймауте)

**Обработка исключений**
- [Q19. (!) Как распространяются исключения в корутинах?](#q19--как-распространяются-исключения-в-корутинах)
- [Q20. (!) Что такое `SupervisorJob` и `supervisorScope`?](#q20--что-такое-supervisorjob-и-supervisorscope)
- [Q21. (!) Что такое `CoroutineExceptionHandler` и где его устанавливать?](#q21--что-такое-coroutineexceptionhandler-и-где-его-устанавливать)
- [Q22. Чем `CancellationException` отличается от обычных исключений?](#q22-чем-cancellationexception-отличается-от-обычных-исключений)

**Flow**
- [Q23. (!) Что такое `Flow` и чем он отличается от `Sequence`?](#q23--что-такое-flow-и-чем-он-отличается-от-sequence)
- [Q24. (!) Что такое cold и hot потоки?](#q24--что-такое-cold-и-hot-потоки)
- [Q25. (!) В чём разница между `StateFlow` и `SharedFlow`?](#q25--в-чём-разница-между-stateflow-и-sharedflow)
- [Q26. Какие операторы `Flow` существуют и как они работают?](#q26-какие-операторы-flow-существуют-и-как-они-работают)
- [Q27. Как обрабатывать ошибки в `Flow`?](#q27-как-обрабатывать-ошибки-в-flow)
- [Q28. Как управлять backpressure в `Flow`?](#q28-как-управлять-backpressure-в-flow)
- [Q29. Как преобразовать cold `Flow` в hot (`shareIn`, `stateIn`)?](#q29-как-преобразовать-cold-flow-в-hot-sharein-statein)

**Channels**
- [Q30. (!) Что такое `Channel` и чем он отличается от `Flow`?](#q30--что-такое-channel-и-чем-он-отличается-от-flow)
- [Q31. Какие типы `Channel` существуют?](#q31-какие-типы-channel-существуют)
- [Q32. Что такое `produce` и `actor`?](#q32-что-такое-produce-и-actor)

**Продвинутые темы**
- [Q33. Как вызывать suspend-функции из обычного кода?](#q33-как-вызывать-suspend-функции-из-обычного-кода)
- [Q34. (!) Распространённые ошибки при работе с корутинами](#q34--распространённые-ошибки-при-работе-с-корутинами)
- [Q35. Как тестировать корутины (`runTest`, `TestScope`)?](#q35-как-тестировать-корутины-runtest-testscope)
- [Q36. Как интегрировать корутины со `Spring`?](#q36-как-интегрировать-корутины-со-spring)
- [Q37. Чем `Flow` отличается от `RxJava`?](#q37-чем-flow-отличается-от-rxjava)
- [Q38. Как работает `Mutex` в корутинах и когда использовать вместо `synchronized`?](#q38-как-работает-mutex-в-корутинах-и-когда-использовать-вместо-synchronized)
- [Q39. Как комбинировать несколько `Flow` — `combine`, `zip`, `merge`?](#q39-как-комбинировать-несколько-flow--combine-zip-merge)

---

## Q1. (!) Что такое корутины и зачем они нужны?

**Корутины** (`Coroutines`) — легковесные единицы выполнения в `Kotlin`, позволяющие писать асинхронный код в последовательном стиле. Они не являются потоками ОС — это абстракция более высокого уровня, управляемая библиотекой `kotlinx.coroutines`.

Ключевые преимущества:
- **Легковесность** — можно запустить десятки тысяч корутин без проблем с памятью (каждая занимает ~несколько сотен байт стека, в отличие от ~1 МБ у потока)
- **Последовательный стиль** — асинхронный код читается как синхронный, без callback hell
- **Кооперативная многозадачность** — корутина сама решает, когда приостановиться (в точках `suspend`)
- **Structured concurrency** — жизненный цикл корутин привязан к scope, что исключает утечки

```kotlin
// 100_000 корутин — без проблем
fun main() = runBlocking {
    repeat(100_000) {
        launch {
            delay(1000)
            print(".")
        }
    }
}
// Попробуйте то же с Thread — получите OutOfMemoryError
```

На собеседовании важно подчеркнуть: корутины — это **не потоки**, а задачи, которые выполняются на потоках из пула. Один поток может обслуживать множество корутин благодаря приостановке и возобновлению.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) В чём разница между потоками и корутинами? Частая ошибка в реальном коде.

| Характеристика | `Thread` | `Coroutine` |
|---|---|---|
| Управление | ОС (preemptive) | Библиотека (cooperative) |
| Память | ~1 МБ стека | ~несколько сотен байт |
| Создание | Дорого (системный вызов) | Дёшево (объект в heap) |
| Переключение контекста | Через ОС, дорого | В user-space, дёшево |
| Масштабируемость | Тысячи — предел | Миллионы — реально |
| Блокирование | Блокирует поток ОС | Приостанавливает, освобождая поток |

```mermaid
graph TD
    subgraph "Потоки ОС"
        T1[Thread 1]
        T2[Thread 2]
    end
    subgraph "Корутины"
        C1[Coroutine A] -->|suspend| T1
        C2[Coroutine B] -->|suspend| T1
        C3[Coroutine C] -->|suspend| T2
        C4[Coroutine D] -->|suspend| T2
        C5[Coroutine E] -->|suspend| T1
    end
```

Ключевое отличие: поток блокируется при ожидании I/O и простаивает, а корутина **приостанавливается**, освобождая поток для другой работы. Это позволяет обслуживать тысячи одновременных запросов малым числом потоков.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Что такое `suspend`-функция и как она работает под капотом? Частая ошибка в реальном коде.

`suspend`-функция — функция, которая может быть приостановлена без блокировки потока и позже возобновлена. Вызывать её можно только из другой `suspend`-функции или из корутины.

```kotlin
suspend fun fetchUser(id: Long): User {
    val response = httpClient.get("/users/$id") // приостановка на I/O
    return response.body()                       // возобновление
}

suspend fun fetchUserWithPosts(id: Long): UserWithPosts {
    val user = fetchUser(id)                     // последовательный вызов
    val posts = fetchPosts(user.id)              // ещё одна приостановка
    return UserWithPosts(user, posts)
}
```

**Под капотом** компилятор Kotlin преобразует `suspend`-функцию в конечный автомат (state machine) через **Continuation Passing Style (CPS)**:

```mermaid
stateDiagram-v2
    [*] --> Label0: вызов функции
    Label0 --> Suspended1: suspend-вызов (fetchUser)
    Suspended1 --> Label1: возобновление с результатом
    Label1 --> Suspended2: suspend-вызов (fetchPosts)
    Suspended2 --> Label2: возобновление с результатом
    Label2 --> [*]: return результат
```

Компилятор добавляет скрытый параметр `Continuation<T>` к каждой `suspend`-функции. На уровне JVM сигнатура `suspend fun fetchUser(id: Long): User` превращается в `fun fetchUser(id: Long, cont: Continuation<User>): Any?`, где возвращаемое значение `COROUTINE_SUSPENDED` сигнализирует о приостановке.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Как определить и запустить корутину? Частая ошибка в реальном коде.

Для запуска корутины нужны две вещи: `CoroutineScope` и **билдер корутины** (`launch`, `async`, `runBlocking`).

```kotlin
// 1. Через runBlocking (блокирует текущий поток — только для main/тестов)
fun main() = runBlocking {
    launch {
        delay(100)
        println("World")
    }
    println("Hello")
}

// 2. Через CoroutineScope (рекомендуемый подход)
class UserService(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    fun refreshUsers() {
        scope.launch {
            val users = fetchUsers()
            cacheUsers(users)
        }
    }

    fun close() {
        scope.cancel() // отменяем все корутины при завершении
    }
}

// 3. Через coroutineScope (suspend-функция, структурированная)
suspend fun loadData() = coroutineScope {
    val user = async { fetchUser() }
    val posts = async { fetchPosts() }
    combine(user.await(), posts.await())
}
```

Не используйте `GlobalScope` в production-коде — это нарушает [structured concurrency](kotlin-coroutines-interview.md) и приводит к утечкам. Подробнее в вопросе Q12.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) В чём разница между `launch` и `async`? Частая ошибка в реальном коде.

| | `launch` | `async` |
|---|---|---|
| Возвращает | `Job` | `Deferred<T>` (наследник `Job`) |
| Результат | Нет (fire-and-forget) | Получается через `await()` |
| Исключения | Пробрасываются в scope | Хранятся в `Deferred`, пробрасываются при `await()` |
| Когда использовать | Побочные эффекты, фоновые задачи | Параллельные вычисления с результатом |

```kotlin
// launch — для задач без результата
scope.launch {
    saveToDatabase(user)
    sendNotification(user.email)
}

// async — для параллельных вычислений
suspend fun loadDashboard(): Dashboard = coroutineScope {
    val profile = async { fetchProfile() }
    val orders = async { fetchOrders() }
    val recommendations = async { fetchRecommendations() }

    Dashboard(
        profile = profile.await(),
        orders = orders.await(),
        recommendations = recommendations.await()
    )
}
```

Частая ошибка: использование `async` без `await()`. В этом случае исключение внутри `async` будет потеряно (точнее, всё равно отменит родительский scope, но stacktrace может быть неочевидным).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. В чём разница между асинхронностью и параллелизмом? Частая ошибка в реальном коде.

**Асинхронность** (concurrency) — способность обрабатывать несколько задач, переключаясь между ними. Задачи могут выполняться на одном потоке, чередуясь в точках приостановки.

**Параллелизм** (parallelism) — одновременное выполнение задач на разных ядрах/потоках.

```kotlin
// Асинхронность без параллелизма (один поток, Dispatchers.Main)
suspend fun loadSequential() {
    val user = fetchUser()    // приостановка → поток свободен
    val posts = fetchPosts()  // приостановка → поток свободен
    show(user, posts)
}

// Параллелизм (задачи реально выполняются одновременно)
suspend fun loadParallel() = coroutineScope {
    val user = async(Dispatchers.IO) { fetchUser() }
    val posts = async(Dispatchers.IO) { fetchPosts() }
    show(user.await(), posts.await()) // обе загрузки идут одновременно
}
```

Корутины дают **асинхронность** из коробки. **Параллелизм** зависит от диспетчера: `Dispatchers.Default` использует пул потоков, равный числу ядер CPU.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. (!) Что такое `CoroutineContext` и из чего он состоит? Частая ошибка в реальном коде.

`CoroutineContext` — набор элементов, определяющих поведение корутины. Это `immutable` ассоциативная коллекция по ключам типов.

Основные элементы:

```mermaid
graph LR
    CC[CoroutineContext] --> J[Job]
    CC --> D[CoroutineDispatcher]
    CC --> N[CoroutineName]
    CC --> EH[CoroutineExceptionHandler]
    J --> |"управление<br>жизненным циклом"| J
    D --> |"на каком потоке<br>выполнять"| D
    N --> |"имя для<br>отладки"| N
    EH --> |"обработка<br>ошибок"| EH
```

```kotlin
// Контексты можно складывать оператором +
val context = Dispatchers.IO + CoroutineName("data-loader") + SupervisorJob()

val scope = CoroutineScope(context)

// Каждая корутина наследует контекст родителя с переопределением
scope.launch(Dispatchers.Default) {
    // Job — дочерний от scope.coroutineContext[Job]
    // Dispatcher — Dispatchers.Default (переопределён)
    // Name — "data-loader" (унаследовано)
}
```

Формула наследования: `childContext = parentContext + childOverrides + Job()`. Дочерняя корутина всегда получает новый `Job`, который становится потомком родительского.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) Какие `Dispatchers` существуют и когда какой использовать? Частая ошибка в реальном коде.

| Диспетчер | Пул потоков | Когда использовать |
|---|---|---|
| `Dispatchers.Default` | Общий пул, размер = CPU cores | CPU-интенсивные вычисления, сортировки, парсинг |
| `Dispatchers.IO` | Эластичный пул, до 64 потоков | Блокирующий I/O: файлы, JDBC, сеть |
| `Dispatchers.Main` | Главный поток UI | Обновление UI (Android) |
| `Dispatchers.Unconfined` | Без привязки к потоку | Тесты, специфические сценарии |

```kotlin
suspend fun processData(data: List<Item>) = coroutineScope {
    // CPU-работа на Default
    val processed = withContext(Dispatchers.Default) {
        data.map { heavyComputation(it) }
    }

    // I/O-работа на IO
    withContext(Dispatchers.IO) {
        database.saveAll(processed)
    }
}
```

`Dispatchers.Default` и `Dispatchers.IO` делят потоки — переключение между ними не вызывает реального переключения контекста потока, если поток уже подходит.

`Dispatchers.IO` можно расширить через `limitedParallelism(n)` для изоляции:

```kotlin
// Выделенный пул из 4 потоков для тяжёлых I/O-операций
val dbDispatcher = Dispatchers.IO.limitedParallelism(4)

suspend fun queryDb() = withContext(dbDispatcher) {
    jdbc.query("SELECT * FROM users")
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Что такое `withContext` и чем отличается от `async`? Частая ошибка в реальном коде.

`withContext` — suspend-функция, переключающая контекст выполнения и **последовательно** ожидающая результат. `async` — запускает **параллельную** корутину и возвращает `Deferred`.

```kotlin
// withContext — последовательное переключение
suspend fun loadUser(): User = withContext(Dispatchers.IO) {
    repository.findUser()  // переключились на IO, дождались результата
}

// async — параллельный запуск
suspend fun loadAll() = coroutineScope {
    val user = async(Dispatchers.IO) { repository.findUser() }
    val orders = async(Dispatchers.IO) { repository.findOrders() }
    Pair(user.await(), orders.await())  // оба запроса параллельно
}
```

Правило: если вам нужен один результат и последовательное переключение контекста — используйте `withContext`. Если нужно запустить несколько задач параллельно — используйте `async` + `await`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) Что такое `structured concurrency`? Частая ошибка в реальном коде.

**Structured concurrency** — принцип, при котором жизненный цикл корутин строго привязан к области (`scope`). Ключевые гарантии:

1. **Корутина не может «утечь»** — она всегда запускается внутри scope
2. **Родитель ждёт детей** — scope не завершится, пока все дочерние корутины не завершатся
3. **Отмена распространяется вниз** — отмена scope отменяет всех детей
4. **Ошибки распространяются вверх** — исключение в ребёнке отменяет родителя (если не `SupervisorJob`)

```mermaid
graph TD
    S[CoroutineScope] --> P[Parent Job]
    P --> C1[Child 1<br>launch]
    P --> C2[Child 2<br>async]
    C2 --> C3[Grandchild<br>launch]

    style S fill:#4a9eff,color:#fff
    P -->|"cancel()"| C1
    P -->|"cancel()"| C2
    C2 -->|"cancel()"| C3
```

```kotlin
suspend fun fetchAllData() = coroutineScope {
    // Если любой async упадёт — отменятся все остальные
    val users = async { fetchUsers() }
    val products = async { fetchProducts() }

    processData(users.await(), products.await())
}
// Вызывающий код гарантированно получит результат или исключение
// Никаких "забытых" корутин, работающих в фоне
```

Без structured concurrency (например, при использовании `GlobalScope`) легко получить «зомби-корутины», которые продолжают работать после уничтожения компонента.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. (!) Что такое `CoroutineScope` и зачем он нужен? Частая ошибка в реальном коде.

`CoroutineScope` — интерфейс с единственным свойством `coroutineContext`. Он задаёт границу жизненного цикла корутин и обеспечивает structured concurrency.

```kotlin
// Создание scope с привязкой к жизненному циклу
class OrderService : AutoCloseable {
    private val scope = CoroutineScope(
        Dispatchers.Default + SupervisorJob() + CoroutineName("order-service")
    )

    fun processOrderAsync(order: Order) {
        scope.launch {
            validateOrder(order)
            saveOrder(order)
            notifyCustomer(order)
        }
    }

    override fun close() {
        scope.cancel() // все корутины будут отменены
    }
}
```

В Android доступны готовые scopes:
- `viewModelScope` — привязан к жизненному циклу `ViewModel`
- `lifecycleScope` — привязан к `Lifecycle` компонента

В серверных приложениях scope обычно создаётся вручную и привязывается к жизненному циклу сервиса или запроса.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. В чём разница между `GlobalScope` и scope с жизненным циклом? Частая ошибка в реальном коде.

`GlobalScope` — scope с жизненным циклом приложения. Корутины в нём **не отменяются автоматически** при уничтожении компонента.

```kotlin
// ПЛОХО — утечка корутины
class MyViewModel {
    fun load() {
        GlobalScope.launch {
            val data = fetchData() // продолжит работу после уничтожения ViewModel
            updateUI(data)         // crash: ViewModel уже уничтожена
        }
    }
}

// ХОРОШО — корутина отменится вместе с ViewModel
class MyViewModel : ViewModel() {
    fun load() {
        viewModelScope.launch {
            val data = fetchData()
            updateUI(data)
        }
    }
}
```

`GlobalScope` допустим только для операций, которые должны жить всё время работы приложения (фоновая синхронизация, метрики). В остальных случаях — scope с жизненным циклом.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Что такое `coroutineScope` (функция) и чем отличается от `CoroutineScope` (конструктор)? Частая ошибка в реальном коде.

- `CoroutineScope(context)` — **конструктор**, создающий новый scope для запуска корутин. Не является suspend-функцией.
- `coroutineScope { }` — **suspend-функция**, создающая вложенный scope и ожидающая завершения всех дочерних корутин.

```kotlin
// coroutineScope — приостанавливает до завершения всех детей
suspend fun loadAllData(): Pair<User, List<Post>> = coroutineScope {
    val user = async { fetchUser() }
    val posts = async { fetchPosts() }
    Pair(user.await(), posts.await())
}
// Вызывающая корутина продолжится только когда обе задачи завершатся

// CoroutineScope — создаёт независимый scope
val myScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
myScope.launch { /* живёт независимо от вызывающего кода */ }
```

`coroutineScope` используют для параллельной декомпозиции внутри suspend-функций. `CoroutineScope` — для создания scope с явным управлением жизненным циклом.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Что такое `runBlocking` и когда его использовать? Частая ошибка в реальном коде.

`runBlocking` — **блокирующий** строитель корутин: он блокирует текущий поток до завершения всех корутин внутри блока.

```kotlin
// Допустимо: main-функция
fun main() = runBlocking {
    val result = async { computeAnswer() }
    println("Answer: ${result.await()}")
}

// Допустимо: тесты
@Test
fun `test coroutine`() = runBlocking {
    val result = myService.fetchData()
    assertEquals("expected", result)
}

// НЕДОПУСТИМО: внутри корутины или на Dispatchers.Main
scope.launch {
    runBlocking { /* DEADLOCK! */ }
}
```

В production-серверном коде `runBlocking` использовать не следует — он блокирует поток и может привести к `deadlock`. Используйте `suspend`-функции и `coroutineScope` вместо этого.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. (!) Что такое `Job` и каков его жизненный цикл? Частая ошибка в реальном коде.

`Job` — элемент `CoroutineContext`, представляющий управляемую единицу работы. Каждая корутина имеет свой `Job`.

```mermaid
stateDiagram-v2
    [*] --> New: Job()
    New --> Active: start() / launch
    Active --> Completing: завершение кода
    Completing --> Completed: все дети завершились
    Active --> Cancelling: cancel() / исключение
    Cancelling --> Cancelled: все дети отменены
    Completed --> [*]
    Cancelled --> [*]
```

```kotlin
val job = scope.launch {
    println("Working...")
    delay(1000)
    println("Done")
}

println(job.isActive)      // true
println(job.isCompleted)   // false
println(job.isCancelled)   // false

job.cancel()               // переход в Cancelling
job.join()                 // ожидание завершения

println(job.isCancelled)   // true
println(job.isCompleted)   // true (Cancelled — финальное состояние)
```

Разница между `Job` и `CoroutineScope`: `Job` управляет состоянием одной корутины (или иерархии), а `CoroutineScope` — контейнер контекста для запуска корутин. Scope содержит `Job` как элемент контекста.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Как отменить корутину и почему отмена кооперативна? Частая ошибка в реальном коде.

Отмена в корутинах **кооперативна**: вызов `cancel()` лишь устанавливает флаг, а корутина должна сама проверять его в точках приостановки.

```kotlin
val job = scope.launch {
    repeat(1000) { i ->
        println("Processing $i")
        delay(100) // <-- проверка отмены происходит здесь
    }
}

delay(350)
job.cancel()    // устанавливает флаг
job.join()      // ждём завершения
// или job.cancelAndJoin()
```

Если корутина выполняет CPU-работу без точек приостановки, она **не отменится**:

```kotlin
// ПЛОХО — не отменяется
val job = scope.launch(Dispatchers.Default) {
    var i = 0
    while (i < 1_000_000) {
        // Тяжёлое вычисление без проверки отмены
        i++
    }
}

// ХОРОШО — проверяем отмену
val job = scope.launch(Dispatchers.Default) {
    var i = 0
    while (isActive && i < 1_000_000) { // проверяем isActive
        i++
    }
}
```

Все стандартные suspend-функции (`delay`, `yield`, `withContext`, операции I/O) проверяют отмену.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. Что такое `yield` и `ensureActive`? Частая ошибка в реальном коде.

Обе функции обеспечивают кооперативную проверку отмены:

| | `yield()` | `ensureActive()` |
|---|---|---|
| Тип | `suspend`-функция | Обычная функция |
| Действие | Приостанавливает корутину, отдаёт поток другим, проверяет отмену | Только проверяет `isActive`, бросает `CancellationException` |
| Когда использовать | В тяжёлых циклах, когда нужно дать другим корутинам поработать | Когда нужна только проверка отмены без приостановки |

```kotlin
// yield — приостанавливает и проверяет отмену
scope.launch {
    hugeList.forEach { item ->
        processItem(item)
        yield() // даём другим корутинам шанс выполниться
    }
}

// ensureActive — только проверяет, не приостанавливает
scope.launch {
    hugeList.forEach { item ->
        ensureActive() // бросит CancellationException если отменена
        processItem(item)
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Как обеспечить отмену при таймауте? Частая ошибка в реальном коде.

`withTimeout` и `withTimeoutOrNull` ограничивают время выполнения блока:

```kotlin
// withTimeout — бросает TimeoutCancellationException
try {
    val result = withTimeout(3000) {
        fetchDataFromSlowApi()
    }
} catch (e: TimeoutCancellationException) {
    println("Запрос превысил 3 секунды")
}

// withTimeoutOrNull — возвращает null при таймауте (удобнее)
val result = withTimeoutOrNull(3000) {
    fetchDataFromSlowApi()
} ?: fallbackValue
```

Таймаут работает кооперативно: если внутри блока нет точек приостановки, отмена сработает только при следующей проверке. Для гарантии добавляйте `ensureActive()` или `yield()` в тяжёлых вычислениях.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. (!) Как распространяются исключения в корутинах? Частая ошибка в реальном коде.

Правила распространения зависят от билдера:

- **`launch`** — исключение немедленно отменяет родительский `Job` и всех его детей
- **`async`** — исключение сохраняется в `Deferred` и пробрасывается при вызове `await()`

```mermaid
graph TD
    S[Scope] --> P[Parent Job]
    P --> C1[Child 1<br>launch ✓]
    P --> C2[Child 2<br>launch ✗ Exception]
    P --> C3[Child 3<br>launch ✓]

    C2 -->|"Exception<br>распространяется вверх"| P
    P -->|"cancel()"| C1
    P -->|"cancel()"| C3

    style C2 fill:#ff4444,color:#fff
    style C1 fill:#ffaa44,color:#fff
    style C3 fill:#ffaa44,color:#fff
```

```kotlin
// Исключение в одном ребёнке отменяет всех
scope.launch {
    launch { fetchUsers() }           // будет отменена
    launch { error("Boom!") }         // ← исключение
    launch { fetchProducts() }        // будет отменена
}
```

Подробнее об обработке исключений — в [вопросах по исключениям Kotlin](kotlin-exceptions-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. (!) Что такое `SupervisorJob` и `supervisorScope`? Частая ошибка в реальном коде.

`SupervisorJob` — разновидность `Job`, при которой **сбой одного ребёнка не отменяет остальных**. Ошибка распространяется вверх, но не «в стороны».

```mermaid
graph TD
    S[Scope + SupervisorJob] --> SJ[SupervisorJob]
    SJ --> C1[Child 1 ✓]
    SJ --> C2[Child 2 ✗ Exception]
    SJ --> C3[Child 3 ✓]

    C2 -->|"Exception"| SJ
    SJ -.->|"НЕ отменяет"| C1
    SJ -.->|"НЕ отменяет"| C3

    style C2 fill:#ff4444,color:#fff
    style C1 fill:#44bb44,color:#fff
    style C3 fill:#44bb44,color:#fff
```

```kotlin
// SupervisorJob в scope — дети независимы
val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

scope.launch { fetchUsers() }         // продолжит работу
scope.launch { error("Boom!") }       // упадёт, но не отменит остальных
scope.launch { fetchProducts() }      // продолжит работу

// supervisorScope — аналог для suspend-функций
suspend fun loadDashboard() = supervisorScope {
    val users = async { fetchUsers() }
    val recommendations = async { fetchRecommendations() } // может упасть
    try {
        DashboardData(users.await(), recommendations.await())
    } catch (e: Exception) {
        DashboardData(users.await(), emptyList())
    }
}
```

Частая ошибка: передача `SupervisorJob()` в `launch`. Это **не работает**, потому что создаётся новый `Job` — потомок `SupervisorJob`, а корутина получает обычный `Job`. Правильно — использовать `supervisorScope` или создавать `CoroutineScope(SupervisorJob())`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. (!) Что такое `CoroutineExceptionHandler` и где его устанавливать? Частая ошибка в реальном коде.

`CoroutineExceptionHandler` — элемент `CoroutineContext`, обрабатывающий **необработанные** исключения.

```kotlin
val handler = CoroutineExceptionHandler { context, exception ->
    log.error("Coroutine ${context[CoroutineName]} failed", exception)
}

val scope = CoroutineScope(SupervisorJob() + handler)

scope.launch {
    error("Boom!") // обработается handler'ом
}
```

Правила:
- Работает **только** с `launch` (не с `async` — там исключение пробрасывается через `await()`)
- Устанавливается на **корневой** корутине или на scope. На дочерней корутине — бесполезен
- **Не перехватывает** `CancellationException` — отмена не считается ошибкой
- С обычным `Job` — handler вызывается после того, как всё уже отменено. С `SupervisorJob` — до отмены других детей (потому что отмены и не будет)


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. Чем `CancellationException` отличается от обычных исключений? Частая ошибка в реальном коде.

`CancellationException` — специальное исключение, означающее **нормальную отмену**, а не ошибку:

| Поведение | `CancellationException` | Обычное исключение |
|---|---|---|
| Отменяет детей | Да | Да |
| Отменяет родителя | **Нет** | Да |
| Обрабатывается `CoroutineExceptionHandler` | **Нет** | Да |
| Логируется | **Нет** (по умолчанию) | Да |

```kotlin
scope.launch {
    launch {
        throw CancellationException("Отменяю себя")
        // Родитель НЕ будет отменён
    }
    launch {
        throw RuntimeException("Ошибка!")
        // Родитель БУДЕТ отменён, все дети тоже
    }
}
```

Не глотайте `CancellationException` в catch-блоках:

```kotlin
// ПЛОХО — корутина не отменится
try {
    suspendFunction()
} catch (e: Exception) { // ловит и CancellationException!
    log.error("Error", e)
}

// ХОРОШО — пробрасываем CancellationException
try {
    suspendFunction()
} catch (e: CancellationException) {
    throw e // пробрасываем!
} catch (e: Exception) {
    log.error("Error", e)
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. (!) Что такое `Flow` и чем он отличается от `Sequence`? Частая ошибка в реальном коде.

`Flow` — асинхронный холодный поток данных, аналог `Sequence`, но с поддержкой suspend-операций.

| | `Sequence` | `Flow` |
|---|---|---|
| Выполнение | Синхронное | Асинхронное (suspend) |
| Блокирует поток | Да | Нет |
| Контекст | Текущий поток | Можно переключать через `flowOn` |
| Отмена | Нет встроенной | Поддерживает отмену корутины |
| Backpressure | Автоматический (pull) | Автоматический (suspend) |

```kotlin
// Sequence — блокирует поток
fun numbersSequence(): Sequence<Int> = sequence {
    yield(1)
    Thread.sleep(1000) // блокирует!
    yield(2)
}

// Flow — не блокирует
fun numbersFlow(): Flow<Int> = flow {
    emit(1)
    delay(1000) // приостанавливает, не блокирует!
    emit(2)
}

// Сбор Flow
scope.launch {
    numbersFlow()
        .filter { it > 0 }
        .map { it * 2 }
        .collect { value ->
            println(value) // 2, 4
        }
}
```

`Flow` начинает выполнение только при вызове терминального оператора (`collect`, `toList`, `first` и др.) — это **холодный** поток.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. (!) Что такое cold и hot потоки? Частая ошибка в реальном коде.

**Cold поток** (`Flow`) — выполнение начинается только при подписке. Каждый коллектор получает свой экземпляр данных.

**Hot поток** (`StateFlow`, `SharedFlow`, `Channel`) — данные эмитируются независимо от подписчиков.

```kotlin
// Cold Flow — каждый collect запускает flow заново
val coldFlow = flow {
    println("Producing...") // выведется дважды
    emit(fetchFromNetwork())
}

coldFlow.collect { println(it) } // Producing... + результат
coldFlow.collect { println(it) } // Producing... + новый результат

// Hot StateFlow — одно значение для всех подписчиков
val _state = MutableStateFlow(0)
val state: StateFlow<Int> = _state.asStateFlow()

scope.launch { state.collect { println("Sub1: $it") } }
scope.launch { state.collect { println("Sub2: $it") } }

_state.value = 42 // оба подписчика получат 42
```

```mermaid
graph LR
    subgraph "Cold Flow"
        P1[Producer] -->|"collect #1"| C1[Collector 1]
        P2[Producer copy] -->|"collect #2"| C2[Collector 2]
    end
    subgraph "Hot Flow"
        HP[Producer] --> SC1[Subscriber 1]
        HP --> SC2[Subscriber 2]
        HP --> SC3[Subscriber 3]
    end
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. (!) В чём разница между `StateFlow` и `SharedFlow`? Частая ошибка в реальном коде.

| | `StateFlow` | `SharedFlow` |
|---|---|---|
| Значение | Всегда имеет текущее (`value`) | Нет свойства `value` |
| Replay | Фиксированно `1` (последнее значение) | Настраиваемый `replay` (0, 1, N) |
| Дедупликация | Да (`distinctUntilChanged`) | Нет |
| Начальное значение | Обязательно | Не требуется |
| Use case | Состояние UI, конфигурация | События, команды, мультикаст |

```kotlin
// StateFlow — хранит текущее состояние
class UserViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun loadUser() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val user = repository.fetchUser()
            _uiState.value = UiState.Success(user)
        }
    }
}

// SharedFlow — события (одноразовые сообщения)
class EventBus {
    private val _events = MutableSharedFlow<Event>(
        replay = 0,            // не повторять старые события новым подписчикам
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<Event> = _events.asSharedFlow()

    suspend fun emit(event: Event) {
        _events.emit(event)
    }
}
```

Правило: `StateFlow` для **состояния** (всегда есть текущее значение, новый подписчик получает его сразу). `SharedFlow` для **событий** (навигация, уведомления, ошибки — не нужно повторять при переподписке).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. Какие операторы `Flow` существуют и как они работают? Частая ошибка в реальном коде.

Операторы `Flow` делятся на три категории:

**Промежуточные (intermediate)** — создают новый `Flow`:

```kotlin
flowOf(1, 2, 3, 4, 5)
    .filter { it % 2 == 0 }         // 2, 4
    .map { it * 10 }                 // 20, 40
    .take(1)                         // 20
    .onEach { println("Got: $it") }  // побочный эффект
    .collect { }
```

**Терминальные** — запускают сбор:

```kotlin
val list = flow.toList()             // собрать в список
val first = flow.first()             // первый элемент
val count = flow.count()             // количество
val sum = flow.reduce { a, b -> a + b }
flow.collect { value -> process(value) }
```

**Комбинирующие** — объединяют несколько Flow:

```kotlin
// combine — последнее значение из каждого
val combined = combine(usersFlow, settingsFlow) { users, settings ->
    UiState(users, settings)
}

// zip — попарно (ждёт оба)
val zipped = namesFlow.zip(agesFlow) { name, age -> Person(name, age) }

// flatMapLatest — при новой эмиссии отменяет предыдущую
searchQuery
    .debounce(300)
    .flatMapLatest { query -> searchApi(query) }
    .collect { results -> showResults(results) }

// flatMapConcat — последовательно
// flatMapMerge — параллельно (concurrency параметр)
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. Как обрабатывать ошибки в `Flow`? Частая ошибка в реальном коде.

```kotlin
// catch — перехватывает исключения из upstream
flow {
    emit(fetchData())
}
    .map { transform(it) }
    .catch { e ->
        // Перехватывает ошибки из flow и map (upstream)
        // НЕ перехватывает ошибки из collect (downstream)
        emit(fallbackValue)        // можно эмитировать fallback
        // или: log.error(e)       // просто залогировать
    }
    .collect { value ->
        updateUI(value) // ошибки отсюда НЕ перехватятся catch выше
    }

// onCompletion — вызывается при завершении (аналог finally)
flow
    .onCompletion { cause ->
        if (cause != null) println("Flow failed: $cause")
        else println("Flow completed normally")
    }
    .collect { }

// retry — автоматический повтор при ошибке
flow { emit(fetchFromNetwork()) }
    .retry(3) { cause ->
        cause is IOException  // повторять только для I/O ошибок
    }
    .collect { }
```

Важно: `catch` перехватывает только **upstream** исключения (из операторов выше по цепочке). Исключения в `collect` нужно оборачивать в `try-catch`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. Как управлять backpressure в `Flow`? Частая ошибка в реальном коде.

Backpressure возникает, когда producer эмитирует быстрее, чем consumer обрабатывает. `Flow` решает это через suspend — `emit()` приостанавливается, пока collector не готов. Для тонкой настройки:

```kotlin
// buffer — producer и consumer работают в разных корутинах
flow
    .buffer(capacity = 64) // буфер на 64 элемента
    .collect { slowProcess(it) }

// conflate — пропускает промежуточные значения
sensorDataFlow
    .conflate() // если collector медленный, берёт только последнее
    .collect { updateDisplay(it) }

// collectLatest — отменяет обработку при новом значении
searchQueryFlow
    .collectLatest { query ->
        // при новом query предыдущий поиск отменяется
        val results = search(query)
        showResults(results)
    }
```

```mermaid
graph LR
    subgraph "buffer"
        P1[Producer<br>быстрый] -->|"1,2,3,4"| B[Buffer<br>capacity=N] --> C1[Consumer<br>медленный]
    end
    subgraph "conflate"
        P2[Producer<br>1,2,3] -->|"пропустить 2"| C2[Consumer<br>получит 1,3]
    end
    subgraph "collectLatest"
        P3[Producer<br>A,B,C] -->|"отмена A,B"| C3[Consumer<br>обработает C]
    end
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q29. Как преобразовать cold `Flow` в hot (`shareIn`, `stateIn`)? Частая ошибка в реальном коде.

`shareIn` и `stateIn` превращают cold `Flow` в горячий, разделяя одну подписку между несколькими collectors:

```kotlin
class UserRepository(
    private val api: UserApi,
    private val scope: CoroutineScope
) {
    // stateIn — превращает в StateFlow
    val currentUser: StateFlow<User?> = flow {
        emit(api.fetchCurrentUser())
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000), // останавливается через 5с без подписчиков
        initialValue = null
    )

    // shareIn — превращает в SharedFlow
    val notifications: SharedFlow<Notification> = flow {
        api.notificationStream().collect { emit(it) }
    }.shareIn(
        scope = scope,
        started = SharingStarted.Lazily, // запускается при первом подписчике
        replay = 0
    )
}
```

Стратегии `SharingStarted`:
- `Eagerly` — запускается сразу
- `Lazily` — при первом подписчике, никогда не останавливается
- `WhileSubscribed(stopTimeout, replayExpiration)` — останавливается, когда нет подписчиков


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q30. (!) Что такое `Channel` и чем он отличается от `Flow`? Частая ошибка в реальном коде.

`Channel` — горячий примитив для передачи данных **между корутинами** по принципу «производитель-потребитель». Каждый элемент доставляется **одному** получателю.

| | `Channel` | `Flow` |
|---|---|---|
| Тип | Hot | Cold (по умолчанию) |
| Получатели | Один (fan-out) | Каждый получает все данные |
| Модель | Коммуникация между корутинами | Поток данных (producer → consumer) |
| Буферизация | Настраиваемая | Через оператор `buffer` |
| Жизненный цикл | Требует явного закрытия | Завершается при окончании `flow { }` |

```kotlin
// Channel — передача между корутинами
val channel = Channel<Int>(capacity = Channel.BUFFERED)

// Producer
scope.launch {
    for (i in 1..5) {
        channel.send(i)
        println("Sent: $i")
    }
    channel.close()
}

// Consumer
scope.launch {
    for (value in channel) {
        println("Received: $value")
        delay(1000)
    }
}

// Fan-out: несколько consumer получают разные элементы
repeat(3) { consumerId ->
    scope.launch {
        for (value in channel) {
            println("Consumer $consumerId got $value")
        }
    }
}
```

Правило: `Channel` — для коммуникации между корутинами (очередь задач, fan-out). `Flow` — для потока данных от источника к потребителю с операторами трансформации. Подробнее об аналогах в реактивном программировании — в [вопросах по RxJava](../../reactive/rxjava-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q31. Какие типы `Channel` существуют? Частая ошибка в реальном коде.

| Тип | Capacity | Поведение `send` при полном буфере |
|---|---|---|
| `RENDEZVOUS` (0) | 0 | Приостанавливается, пока receiver не вызовет `receive` |
| `BUFFERED` | 64 (по умолчанию) | Приостанавливается при полном буфере |
| `CONFLATED` | 1 | Перезаписывает последнее значение (никогда не приостанавливается) |
| `UNLIMITED` | ∞ | Никогда не приостанавливается (риск `OutOfMemoryError`) |
| Числовое значение | N | Приостанавливается при N элементах в буфере |

```kotlin
// Rendezvous — producer ждёт consumer
val rendezvous = Channel<Int>(Channel.RENDEZVOUS)

// Buffered — стандартный буферизованный
val buffered = Channel<Int>(Channel.BUFFERED)

// Conflated — хранит только последнее значение
val conflated = Channel<Int>(Channel.CONFLATED)

// Можно указать onBufferOverflow для точной стратегии
val channel = Channel<Int>(
    capacity = 10,
    onBufferOverflow = BufferOverflow.DROP_OLDEST
)
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q32. Что такое `produce` и `actor`? Частая ошибка в реальном коде.

`produce` — билдер корутины, создающий `ReceiveChannel` (producer-паттерн):

```kotlin
// produce — корутина-производитель
fun CoroutineScope.produceNumbers(): ReceiveChannel<Int> = produce {
    var x = 1
    while (true) {
        send(x++)
        delay(100)
    }
}

val numbers = produceNumbers()
repeat(5) {
    println(numbers.receive())
}
numbers.cancel()
```

`actor` (deprecated в пользу других подходов) — корутина-получатель, обрабатывающая сообщения из `SendChannel`. Современная альтернатива — использование `Channel` + `for` loop или `Flow`:

```kotlin
// Современный паттерн actor через Channel
sealed class Command {
    data class Add(val value: Int) : Command()
    object GetTotal : Command()
}

fun CoroutineScope.counterActor() = launch {
    val channel = Channel<Command>()
    var total = 0
    for (cmd in channel) {
        when (cmd) {
            is Command.Add -> total += cmd.value
            is Command.GetTotal -> println("Total: $total")
        }
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q33. Как вызывать suspend-функции из обычного кода? Частая ошибка в реальном коде.

Из не-suspend кода suspend-функцию можно вызвать только через создание корутины:

```kotlin
// 1. runBlocking — блокирует текущий поток (main, тесты, скрипты)
fun main() {
    val result = runBlocking {
        fetchData()
    }
}

// 2. CoroutineScope.launch — fire-and-forget
class MyService {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun doWork() {
        scope.launch {
            val data = fetchData()
            processData(data)
        }
    }
}

// 3. Spring suspend-контроллер — фреймворк создаёт корутину
@RestController
class UserController(private val service: UserService) {
    @GetMapping("/users/{id}")
    suspend fun getUser(@PathVariable id: Long): User {
        return service.findUser(id)  // suspend-функция
    }
}

// 4. CompletableFuture bridge (для Java-interop)
fun fetchDataFuture(): CompletableFuture<Data> =
    scope.future { fetchData() }
```

Подробнее о взаимодействии с Java-кодом — в [вопросах по Kotlin-Java Interop](kotlin-interop-java-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q34. (!) Распространённые ошибки при работе с корутинами Частая ошибка в реальном коде.

**1. Использование `GlobalScope` вместо structured concurrency:**
```kotlin
// ПЛОХО
GlobalScope.launch { fetchData() }

// ХОРОШО
scope.launch { fetchData() }
```

**2. Глотание `CancellationException`:**
```kotlin
// ПЛОХО
try { suspendFun() } catch (e: Exception) { log(e) }

// ХОРОШО
try { suspendFun() } catch (e: CancellationException) { throw e } catch (e: Exception) { log(e) }
```

**3. `SupervisorJob` в `launch` вместо scope:**
```kotlin
// ПЛОХО — SupervisorJob не даёт эффекта
scope.launch(SupervisorJob()) { /* ... */ }

// ХОРОШО
supervisorScope { launch { /* ... */ } }
```

**4. Блокирующий код без переключения диспетчера:**
```kotlin
// ПЛОХО — блокирует Dispatchers.Default/Main
scope.launch { Thread.sleep(1000) }

// ХОРОШО
scope.launch { withContext(Dispatchers.IO) { Thread.sleep(1000) } }
```

**5. Запуск корутины в `init` или конструкторе без scope:**
```kotlin
// ПЛОХО — корутина переживёт объект
class MyClass {
    init { GlobalScope.launch { loadData() } }
}
```

**6. Неправильный сбор `Flow` в UI (Android):**
```kotlin
// ПЛОХО — не пережнвает пересоздание Activity
lifecycleScope.launch { flow.collect { } }

// ХОРОШО
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        flow.collect { }
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q35. Как тестировать корутины (`runTest`, `TestScope`)? Частая ошибка в реальном коде.

Библиотека `kotlinx-coroutines-test` предоставляет инструменты для тестирования с виртуальным временем:

```kotlin
// runTest — основной инструмент (заменяет runBlockingTest)
@Test
fun `test delay is skipped`() = runTest {
    val result = async {
        delay(10_000) // НЕ ждёт реально — виртуальное время
        42
    }
    assertEquals(42, result.await())
}

// Инжектирование TestDispatcher
class UserService(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) {
    suspend fun fetchUser(): User = withContext(dispatcher) {
        repository.findUser()
    }
}

@Test
fun `test with injected dispatcher`() = runTest {
    val service = UserService(dispatcher = UnconfinedTestDispatcher(testScheduler))
    val user = service.fetchUser()
    assertEquals("John", user.name)
}

// Управление виртуальным временем
@Test
fun `test timeout behavior`() = runTest {
    val flow = flow {
        emit(1)
        delay(1000)
        emit(2)
    }

    val values = mutableListOf<Int>()
    flow.collect { values.add(it) }

    assertEquals(listOf(1, 2), values)
    assertEquals(1000, currentTime) // прошло 1000мс виртуального времени
}

// advanceTimeBy / advanceUntilIdle
@Test
fun `test periodic task`() = runTest {
    var count = 0
    val job = launch {
        while (true) {
            delay(1000)
            count++
        }
    }
    advanceTimeBy(3500)
    assertEquals(3, count)
    job.cancel()
}
```

Рекомендация: всегда инжектируйте `CoroutineDispatcher` через конструктор, а в тестах подменяйте на `UnconfinedTestDispatcher` или `StandardTestDispatcher`.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q36. Как интегрировать корутины со `Spring`? Частая ошибка в реальном коде.

`Spring WebFlux` (начиная с Spring 5.2) нативно поддерживает `suspend`-функции и `Flow`:

```kotlin
// Suspend-контроллер
@RestController
class UserController(private val userService: UserService) {

    @GetMapping("/users/{id}")
    suspend fun getUser(@PathVariable id: Long): User {
        return userService.findById(id) // suspend-функция
    }

    @GetMapping("/users")
    fun getAllUsers(): Flow<User> {
        return userService.findAll() // возвращает Flow
    }
}

// Suspend-сервис
@Service
class UserService(private val repository: UserRepository) {

    suspend fun findById(id: Long): User =
        withContext(Dispatchers.IO) {
            repository.findById(id) ?: throw NotFoundException("User $id not found")
        }

    fun findAll(): Flow<User> = flow {
        repository.findAll().forEach { emit(it) }
    }.flowOn(Dispatchers.IO)
}
```

Необходимые зависимости:
```kotlin
implementation("org.springframework.boot:spring-boot-starter-webflux")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
```

`kotlinx-coroutines-reactor` обеспечивает мост между корутинами и `Reactor` (`Mono`/`Flux`). Spring автоматически конвертирует suspend-функции в `Mono`, а `Flow` — в `Flux`.

Для блокирующего стека (`spring-boot-starter-web`) корутины можно запускать вручную через `CoroutineScope` в сервисе, но без нативной поддержки suspend-контроллеров.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q37. Чем `Flow` отличается от `RxJava`? Частая ошибка в реальном коде.

| | `Flow` | `RxJava` |
|---|---|---|
| Зависимость | Часть `kotlinx.coroutines` | Отдельная библиотека |
| Язык | Kotlin-first | Java-first |
| Backpressure | Встроенный через suspend | `Flowable` (отдельный тип) |
| Отмена | Через structured concurrency | `Disposable` + ручное управление |
| Обработка ошибок | `catch`, `retry` + coroutine exception handling | `onError*` операторы |
| Hot streams | `StateFlow`, `SharedFlow` | `Subject`, `Processor` |
| Кривая обучения | Низкая (если знаешь корутины) | Высокая |
| Операторы | ~50 | ~400+ |

```kotlin
// RxJava
Observable.fromCallable { fetchData() }
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(
        { data -> showData(data) },
        { error -> showError(error) }
    )

// Flow — эквивалент
flow { emit(fetchData()) }
    .flowOn(Dispatchers.IO)
    .catch { error -> showError(error) }
    .collect { data -> showData(data) } // уже на нужном потоке
```

`Flow` рекомендуется для новых Kotlin-проектов. `RxJava` по-прежнему актуален в крупных проектах с Java-кодом или богатой операторной базой. Подробнее — в [вопросах по RxJava](../../reactive/rxjava-interview.md).


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q38. Как работает `Mutex` в корутинах и когда использовать вместо `synchronized`? Частая ошибка в реальном коде.

`Mutex` — инструмент взаимного исключения для корутин. В отличие от `synchronized` и `ReentrantLock`, `Mutex` **не блокирует поток** — корутина приостанавливается при ожидании блокировки.

```kotlin
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

val mutex = Mutex()
var counter = 0

// withLock — suspend-функция: приостанавливает, не блокирует поток
suspend fun increment() {
    mutex.withLock {
        counter++
    }
}

// Пример: 100 корутин безопасно инкрементируют счётчик
fun main() = runBlocking {
    val jobs = List(100) {
        launch { repeat(1000) { increment() } }
    }
    jobs.forEach { it.join() }
    println("Counter: $counter")  // 100000
}
```

**Почему не `synchronized` в корутинах:**

```kotlin
// ПЛОХО — synchronized блокирует поток, корутина не может приостановиться внутри
suspend fun dangerousFun() {
    synchronized(lock) {
        delay(100) // IllegalStateException: suspension functions can't be called in sync blocks
    }
}

// ХОРОШО — Mutex позволяет приостановку
suspend fun safeFun() {
    mutex.withLock {
        delay(100) // OK — корутина приостановлена, поток свободен
    }
}
```

| | `synchronized` | `Mutex` |
|---|---|---|
| Блокировка | Блокирует поток ОС | Приостанавливает корутину |
| Suspend-функции внутри | Нельзя | Можно |
| Применение | Java-legacy, non-coroutine код | Корутины |

Для счётчиков без сложной логики предпочтительнее `AtomicInteger` или `AtomicLong` — они не требуют блокировки вовсе.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление## Q39. Как комбинировать несколько `Flow` — `combine`, `zip`, `merge`? Частая ошибка в реальном коде.

Три основных оператора для объединения потоков с разной семантикой:

**`combine`** — каждый раз при поступлении нового значения от **любого** потока вычисляет результат из **последних** значений всех потоков:

```kotlin
val flow1 = flow {
    emit(1); delay(300); emit(2); delay(300); emit(3)
}
val flow2 = flow {
    delay(150); emit("a"); delay(300); emit("b")
}

flow1.combine(flow2) { n, s -> "$n$s" }.collect { println(it) }
// 1a, 2a, 2b, 3b  (при каждом новом значении — пара из последних)
```

**`zip`** — попарное объединение: ждёт пару значений (по одному от каждого потока) и только затем выдаёт результат. Останавливается, когда кончается более короткий поток:

```kotlin
val numbers = flow { emit(1); emit(2); emit(3) }
val letters = flow { emit("A"); emit("B") }

numbers.zip(letters) { n, s -> "$n$s" }.collect { println(it) }
// 1A, 2B  (только 2 пары — по числу элементов более короткого потока)
```

**`merge`** (и `flattenMerge`) — объединяет несколько потоков в один, перемежая их значения в порядке появления:

```kotlin
val flowA = flow { emit("A1"); delay(200); emit("A2") }
val flowB = flow { delay(100); emit("B1"); delay(200); emit("B2") }

merge(flowA, flowB).collect { println(it) }
// A1, B1, A2, B2  (в реальном времени по мере поступления)
```

**`flatMapMerge`** — конкурентная трансформация каждого элемента в Flow:

```kotlin
// Параллельная загрузка данных для каждого id
val ids = flowOf(1, 2, 3)
ids.flatMapMerge { id ->
    flow { emit(loadUser(id)) }
}.collect { user -> process(user) }
```

| Оператор | Семантика | Стоп-условие |
|---|---|---|
| `combine` | Последние значения при любом новом | Оба потока завершились |
| `zip` | Строгие пары | Более короткий поток |
| `merge` | Конкурентное слияние | Все потоки завершились |

---

## See also

- [Kotlin](kotlin-interview.md) — основные вопросы по языку Kotlin, suspend-функции
- [Коллекции в Kotlin](kotlin-collections-interview.md) — Flow как асинхронная альтернатива Sequence
- [DSL в Kotlin](kotlin-dsl-interview.md) — coroutine builders (launch, async) как пример DSL-стиля
- [Сериализация в Kotlin](kotlin-serialization-interview.md) — async-сериализация в Ktor с Flow
- [Kotlin-Java Interop](kotlin-interop-java-interview.md) — вызов suspend-функций из Java
- [Исключения в Kotlin](kotlin-exceptions-interview.md) — CoroutineExceptionHandler, SupervisorJob
- [Java Concurrency](../java/java-concurrency-interview.md) — многопоточность в Java для сравнения моделей
- [RxJava](../../reactive/rxjava-interview.md) — реактивные потоки, сравнение с `Flow`
- [Spring Boot](../../frameworks/spring/spring-boot-interview.md) — интеграция корутин со Spring WebFlux


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения Ключевое отличие и best practice в production.
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие Частая ошибка в реальном коде.
> - [ ] Неправильный вариант 3 | Противоположное направление- [Kotlin коллекции](kotlin-collections-interview.md) Частая ошибка в реальном коде.
- [DSL в Kotlin](kotlin-dsl-interview.md)
- [исключения в Kotlin](kotlin-exceptions-interview.md)
- [интероп Kotlin и Java](kotlin-interop-java-interview.md)
- [Kotlin](kotlin-interview.md)
- [сериализация в Kotlin](kotlin-serialization-interview.md)
