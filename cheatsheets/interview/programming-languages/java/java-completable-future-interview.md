---
title: "Вопросы на собеседовании: Java CompletableFuture"
description: "CompletableFuture: thenApply/thenCompose/thenCombine, allOf/anyOf, обработка ошибок, timeout, ForkJoinPool, join() vs get()"
tags:
  - interview
  - programming-languages
  - java-completable-future-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Java CompletableFuture"
  - "CompletableFuture вопросы"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Java CompletableFuture`

`CompletableFuture<T>` — реализация `Future` с поддержкой неблокирующих цепочек, комбинирования нескольких асинхронных операций и обработки ошибок. Введён в Java 8. На собеседованиях проверяют разницу между `thenApply`/`thenCompose`/`thenCombine`, обработку исключений и работу с пулом потоков.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [CompletableFuture JavaDoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/concurrent/CompletableFuture.html) — полный API
- [Baeldung: CompletableFuture Guide](https://www.baeldung.com/java-completablefuture) — подробный туториал

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Создание и запуск**
- [Q1. (!) Как создать CompletableFuture?](#q1--как-создать-completablefuture)
- [Q2. В каком потоке выполняется задача?](#q2-в-каком-потоке-выполняется-задача)

**Трансформация и цепочки**
- [Q3. (!) Чем thenApply, thenCompose и thenCombine отличаются?](#q3--чем-thenapply-thencompose-и-thencombine-отличаются)
- [Q4. Чем thenAccept отличается от thenRun?](#q4-чем-thenaccept-отличается-от-thenrun)
- [Q5. Что такое Async-суффикс у методов?](#q5-что-такое-async-суффикс-у-методов)

**Комбинирование нескольких Future**
- [Q6. (!) Чем allOf отличается от anyOf?](#q6--чем-allof-отличается-от-anyof)
- [Q7. Как получить результаты из allOf?](#q7-как-получить-результаты-из-allof)

**Обработка ошибок**
- [Q8. (!) Чем exceptionally, handle и whenComplete отличаются?](#q8--чем-exceptionally-handle-и-whencomplete-отличаются)
- [Q9. Как пробросить исключение в CompletableFuture?](#q9-как-пробросить-исключение-в-completablefuture)

**Блокирующие операции**
- [Q10. (!) Чем join() отличается от get()?](#q10--чем-join-отличается-от-get)
- [Q11. Как задать timeout для CompletableFuture?](#q11-как-задать-timeout-для-completablefuture)

**Продвинутые темы**
- [Q12. Что такое complete() и completeExceptionally()?](#q12-что-такое-complete-и-completeexceptionally)
- [Q13. Когда НЕ стоит использовать CompletableFuture?](#q13-когда-не-стоит-использовать-completablefuture)

---

## Q1. (!) Как создать CompletableFuture?

Способ создания зависит от того, кто завершает Future: пул потоков (фабрики `supplyAsync`/`runAsync`), вы сами вручную (конструктор + `complete`) или он уже готов (`completedFuture`).

- `supplyAsync` / `runAsync` — запустить задачу в пуле потоков; `supplyAsync` возвращает результат, `runAsync` — нет (`Void`).
- `completedFuture` — Future, уже завершённый заданным значением; удобен как заглушка в тестах и в ветках, где результат известен сразу.
- `new CompletableFuture<>()` — пустой Future, который вы завершаете сами позже через `complete(...)` или `completeExceptionally(...)`. Так оборачивают колбэки чужих API в Future-стиль (см. Q12).
- Любую фабрику с суффиксом `Async` можно вызвать со своим `Executor` — иначе задача уйдёт в общий `ForkJoinPool.commonPool()`.

```java
// 1. supplyAsync — асинхронная задача, возвращает результат
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Hello");

// 2. runAsync — асинхронная задача без результата
CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> System.out.println("done"));

// 3. completedFuture — уже завершённый Future (удобен в тестах)
CompletableFuture<String> f3 = CompletableFuture.completedFuture("result");

// 4. new CompletableFuture — ручное управление
CompletableFuture<String> f4 = new CompletableFuture<>();
// где-то позже: f4.complete("value") или f4.completeExceptionally(ex)

// 5. С кастомным Executor:
ExecutorService executor = Executors.newFixedThreadPool(4);
CompletableFuture<String> f5 = CompletableFuture.supplyAsync(
    () -> fetchFromApi(), executor
);
```

---

## Q2. В каком потоке выполняется задача?

Поток выбирается по одному правилу: метод **без** суффикса `Async` исполняется там, где завершился предыдущий этап, а метод **с** `Async` уходит в пул потоков. Это ключевая деталь — именно от неё зависит, не залипнет ли цепочка на чужом потоке.

| Вариант | Поток выполнения |
|---|---|
| Без `Async` (thenApply, thenAccept, ...) | Поток, завершивший предыдущий этап (или calling thread если уже завершён) |
| `*Async` без Executor | `ForkJoinPool.commonPool()` |
| `*Async` с Executor | Указанный Executor |

```java
CompletableFuture.supplyAsync(() -> "data")  // ForkJoinPool
    .thenApply(s -> s.toUpperCase())          // тот же поток ForkJoinPool
    .thenApplyAsync(s -> s + "!", executor)   // кастомный executor
    .thenAccept(System.out::println);         // поток executor
```

**Подводный камень:** `ForkJoinPool.commonPool()` — разделяемый на всю JVM пул с `Runtime.availableProcessors() - 1` потоками, и в нём же крутятся параллельные стримы. Потоков мало, поэтому блокирующая IO-задача (HTTP, JDBC) надолго занимает поток и тормозит всё остальное. Для IO заводите отдельный executor:

```java
ExecutorService ioExecutor = Executors.newCachedThreadPool();
CompletableFuture.supplyAsync(() -> httpClient.get(url), ioExecutor);
```

---

## Q3. (!) Чем thenApply, thenCompose и thenCombine отличаются?

Разница в том, что делает функция-аргумент и сколько Future участвует. `thenApply` берёт результат и возвращает обычное значение. `thenCompose` берёт результат и возвращает **новый Future** (поэтому нет вложенности `CF<CF<...>>`). `thenCombine` объединяет результаты **двух** независимых Future. Самый частый вопрос на собесе — про пару `thenApply` vs `thenCompose`: это полный аналог `map` vs `flatMap` в Stream/Optional.

| Метод | Аналог в Stream | Назначение |
|---|---|---|
| `thenApply(T→U)` | `map` | Преобразовать результат |
| `thenCompose(T→CF<U>)` | `flatMap` | Цепочка зависимых Future |
| `thenCombine(CF<U>, BiFunction)` | `zip` | Объединить два независимых Future |

**thenApply — трансформация:**
```java
CompletableFuture<Integer> length = CompletableFuture
    .supplyAsync(() -> "hello")
    .thenApply(String::length);  // "hello" → 5
```

**thenCompose — цепочка зависимых вызовов (аналог flatMap):**
```java
// Сначала найти user по email, потом загрузить его заказы
CompletableFuture<List<Order>> orders = userService
    .findByEmail(email)               // CF<User>
    .thenCompose(user ->              // User → CF<List<Order>>
        orderService.findByUser(user.getId())  // возвращает CF
    );
// Без thenCompose получили бы CF<CF<List<Order>>> — вложенный Future
```

**thenCombine — параллельные независимые задачи:**
```java
CompletableFuture<String> userFuture = userService.fetchUser(id);
CompletableFuture<List<Order>> orderFuture = orderService.fetchOrders(id);

CompletableFuture<UserProfile> profile = userFuture
    .thenCombine(orderFuture,
        (user, orders) -> new UserProfile(user, orders)
    );
// userFuture и orderFuture выполняются параллельно
```

---

## Q4. Чем thenAccept отличается от thenRun?

Оба — терминальные этапы для побочного эффекта без возврата значения (оба отдают `CompletableFuture<Void>`). Единственное отличие — есть ли доступ к результату предыдущего этапа: `thenAccept` принимает `Consumer<T>` и получает результат, `thenRun` принимает `Runnable` и результат не видит.

| | `thenAccept(Consumer<T>)` | `thenRun(Runnable)` |
|---|---|---|
| Получает результат предыдущего этапа | Да | Нет |
| Возвращает | `CompletableFuture<Void>` | `CompletableFuture<Void>` |
| Применение | Потребить результат (save, log) | Выполнить действие без доступа к результату |

```java
CompletableFuture.supplyAsync(() -> fetchUser())
    .thenAccept(user -> userCache.put(user.getId(), user));  // получает user

CompletableFuture.supplyAsync(() -> processData())
    .thenRun(() -> log.info("Processing complete"));  // не знает результат
```

---

## Q5. Что такое Async-суффикс у методов?

Суффикс `Async` управляет тем, **в каком потоке** выполнится этап. У каждого метода трансформации есть пара: обычный и `*Async`-вариант.
- `thenApply` → `thenApplyAsync`
- `thenAccept` → `thenAcceptAsync`
- `thenCompose` → `thenComposeAsync`

Правило простое:
- **Без `Async`** — этап выполняется в том потоке, который завершил предыдущий этап (а если Future уже был завершён к моменту вызова — прямо в вызывающем потоке, синхронно).
- **С `Async`** — этап заведомо отправляется в пул: `ForkJoinPool.commonPool()` или переданный вторым аргументом `Executor`.

```java
// thenApply: продолжение в том же потоке (может быть main thread если CF уже завершён)
future.thenApply(s -> transform(s));

// thenApplyAsync: всегда в пуле потоков
future.thenApplyAsync(s -> transform(s));
future.thenApplyAsync(s -> transform(s), myExecutor);
```

**Когда брать `Async`:** для CPU-интенсивных этапов (чтобы не занимать IO-поток, завершивший предыдущий шаг) или когда нужно гарантированно увести работу с вызывающего потока. Если этап лёгкий, обычная версия дешевле — нет лишнего переключения между потоками.

---

## Q6. (!) Чем allOf отличается от anyOf?

Оба объединяют массив Future, но по разной логике завершения. `allOf` ждёт, пока завершатся **все** (логика «И» — нужны все результаты), `anyOf` завершается, как только готов **любой** (логика «ИЛИ», гонка — берём первый ответ). Отличаются и типы: `allOf` отдаёт `CompletableFuture<Void>` (результаты надо доставать отдельно, см. Q7), `anyOf` — `CompletableFuture<Object>` с результатом победителя.

| | `allOf` | `anyOf` |
|---|---|---|
| Завершается когда | **Все** futures завершены | **Любой** future завершён |
| Возвращает | `CompletableFuture<Void>` | `CompletableFuture<Object>` |
| Применение | Ждать параллельные задачи | Race: взять первый результат |

```java
// allOf — дождаться всех:
CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> fetchA());
CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> fetchB());
CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> fetchC());

CompletableFuture.allOf(f1, f2, f3).join();  // блокирует до завершения всех

// anyOf — взять первый результат:
CompletableFuture<Object> fastest = CompletableFuture.anyOf(f1, f2, f3);
Object result = fastest.join();  // результат самого быстрого
```

**Подводный камень:** `anyOf` смотрит на того, кто завершился **первым**, а не на того, кто завершился успешно. Если самый быстрый Future упал с исключением — упадёт и `anyOf`, даже если остальные потом вернули бы корректный результат. Аналогично `allOf` завершится исключением, если хотя бы один из futures завершился ошибкой.

---

## Q7. Как получить результаты из allOf?

`allOf` возвращает `CompletableFuture<Void>`, то есть сам по себе он лишь сигнал «все готовы», а не контейнер с результатами. Поэтому шаблон такой: дождаться `allOf(...).join()`, а затем вызвать `join()` у каждого исходного Future. Ключевой момент — после завершения `allOf` все исходные futures уже завершены, так что эти `join()` **не блокируют** и не запускают работу заново, а просто отдают готовый результат.

```java
CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> fetchUser(id));
CompletableFuture<List<Order>> orderFuture = CompletableFuture.supplyAsync(() -> fetchOrders(id));
CompletableFuture<AccountInfo> accountFuture = CompletableFuture.supplyAsync(() -> fetchAccount(id));

CompletableFuture.allOf(userFuture, orderFuture, accountFuture).join();

// Теперь все завершены, join() не блокирует:
User user = userFuture.join();
List<Order> orders = orderFuture.join();
AccountInfo account = accountFuture.join();

UserDashboard dashboard = new UserDashboard(user, orders, account);
```

**Для однотипных futures:**
```java
List<CompletableFuture<String>> futures = ids.stream()
    .map(id -> CompletableFuture.supplyAsync(() -> fetchById(id)))
    .collect(Collectors.toList());

CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

List<String> results = futures.stream()
    .map(CompletableFuture::join)  // уже завершены, нет блокировки
    .collect(Collectors.toList());
```

---

## Q8. (!) Чем exceptionally, handle и whenComplete отличаются?

Различать их проще по двум осям: **когда срабатывает** и **может ли подменить результат**.
- `exceptionally` — срабатывает только при ошибке и подменяет её на запасное значение. Это аналог `catch`: «если упало — верни дефолт».
- `handle` — срабатывает всегда (и при успехе, и при ошибке), получает пару `(результат, исключение)` и возвращает новое значение. Универсальный преобразователь итога; может даже сменить тип результата.
- `whenComplete` — срабатывает всегда, но результат и исключение **не меняет**: он их только наблюдает и прокидывает дальше как есть. Это аналог `finally` — для логирования, метрик, очистки ресурсов.

| Метод | Когда вызывается | Доступен результат | Доступно исключение | Возвращает новый тип |
|---|---|---|---|---|
| `exceptionally(ex → T)` | Только при ошибке | Нет | Да | Да |
| `handle((T, ex) → U)` | Всегда | Да | Да | Да |
| `whenComplete((T, ex) → void)` | Всегда | Да | Да | Нет (void) |

```java
// exceptionally — recovery при ошибке, возвращает дефолт
CompletableFuture<String> result = fetchUser(id)
    .exceptionally(ex -> {
        log.error("Failed to fetch user", ex);
        return "AnonymousUser";  // возвращаем дефолт
    });

// handle — трансформация в любом случае
CompletableFuture<Optional<String>> result2 = fetchUser(id)
    .handle((user, ex) -> ex != null
        ? Optional.empty()
        : Optional.of(user));

// whenComplete — side effect (logging), не меняет результат/исключение
CompletableFuture<String> traced = fetchUser(id)
    .whenComplete((user, ex) -> {
        if (ex != null) metrics.incrementError();
        else metrics.incrementSuccess();
    });
```

---

## Q9. Как пробросить исключение в CompletableFuture?

Способ зависит от того, как Future создан. Если вы управляете им вручную — завершаете через `completeExceptionally(...)`. Если задача внутри лямбды `supplyAsync` — любое unchecked-исключение перехватывается автоматически и переводит Future в ошибочное состояние. А вот checked-исключение в лямбду не пробросишь напрямую (сигнатуры `Supplier`/`Function` его не объявляют) — его нужно обернуть, обычно в `CompletionException`.

Во всех случаях исключение всплывёт при попытке достать результат: `join()` бросит `CompletionException`, а `get()` — `ExecutionException` (см. Q10).

```java
// 1. Через completeExceptionally (для ручного управления):
CompletableFuture<String> future = new CompletableFuture<>();
future.completeExceptionally(new RuntimeException("failed"));
// При join() или get() → CompletionException/ExecutionException

// 2. Из лямбды supplyAsync — любое unchecked исключение автоматически:
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
    if (condition) throw new RuntimeException("error");  // propagates
    return "result";
});

// 3. Checked exception — нужно обернуть:
CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
    try {
        return readFile();
    } catch (IOException e) {
        throw new CompletionException(e);  // wrapped
    }
});
```

---

## Q10. (!) Чем join() отличается от get()?

Оба метода блокируют поток и возвращают результат, но различаются типом исключения. `join()` бросает **unchecked** `CompletionException` и не объявляет проверяемых исключений — поэтому его удобно вызывать прямо в лямбдах Stream API. `get()` — это метод из интерфейса `Future`: он бросает **checked** `ExecutionException` и `InterruptedException`, которые приходится явно обрабатывать, зато только у `get()` есть перегрузка с таймаутом — `get(timeout, unit)`.

| | `join()` | `get()` |
|---|---|---|
| Исключение | `CompletionException` (unchecked) | `ExecutionException` (checked), `InterruptedException` |
| Использование в Stream | Удобно (нет try-catch) | Неудобно (нужен try-catch) |
| Timeout | Нет | `get(timeout, unit)` |

```java
// join() — проще в Stream API
List<String> results = futures.stream()
    .map(CompletableFuture::join)  // нет checked exceptions
    .collect(Collectors.toList());

// get() — классический Future API
try {
    String result = future.get(5, TimeUnit.SECONDS);
} catch (ExecutionException e) {
    log.error("Task failed", e.getCause());
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} catch (TimeoutException e) {
    log.warn("Timeout");
}
```

**Главное:** оба **блокируют** текущий поток до завершения Future. Вызывайте их только там, где блокировка допустима (на границе пайплайна, в тестах) — иначе теряется весь смысл асинхронности.

---

## Q11. Как задать timeout для CompletableFuture?

Начиная с Java 9 есть два штатных метода: `orTimeout` (упасть с `TimeoutException`, если не успели) и `completeOnTimeout` (подставить дефолтное значение по таймауту). В Java 8 их нет — таймаут эмулируют через отдельный `ScheduledExecutorService` и `applyToEither`: кто из двух Future (рабочий или «будильник») сработает первым, тот и определит исход.

Важно понимать, что таймаут здесь — это про **результат**, а не про отмену самой задачи: исходная работа в пуле продолжит выполняться, просто её итог уже никого не интересует.

**Java 9+:**
```java
// orTimeout — завершается TimeoutException если не готово вовремя
CompletableFuture<String> withTimeout = fetchUser(id)
    .orTimeout(3, TimeUnit.SECONDS);
// Исключение: CompletionException(TimeoutException)

// completeOnTimeout — возвращает дефолт при таймауте
CompletableFuture<String> withDefault = fetchUser(id)
    .completeOnTimeout("DefaultUser", 3, TimeUnit.SECONDS);
```

**Java 8 (workaround):**
```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

public static <T> CompletableFuture<T> withTimeout(
        CompletableFuture<T> future, long timeout, TimeUnit unit) {
    CompletableFuture<T> timeoutFuture = new CompletableFuture<>();
    scheduler.schedule(
        () -> timeoutFuture.completeExceptionally(new TimeoutException()),
        timeout, unit);
    return future.applyToEither(timeoutFuture, t -> t);
}
```

---

## Q12. Что такое complete() и completeExceptionally()?

Это методы, которыми вы **сами** завершаете Future извне: `complete(value)` переводит его в успешное состояние, `completeExceptionally(ex)` — в ошибочное. Они отличают `CompletableFuture` от обычного `Future`, результат которого задаёт только исполняющая задача. Главный сценарий — мост между callback-API (старые клиенты, JMS, низкоуровневый NIO) и Future-стилем: создаём пустой Future, отдаём его наружу, а в колбэках `onSuccess`/`onError` завершаем нужным методом.

```java
// Адаптер колбэка → CompletableFuture
public CompletableFuture<String> fetchAsync() {
    CompletableFuture<String> future = new CompletableFuture<>();

    legacyClient.fetch(url, new Callback() {
        @Override
        public void onSuccess(String result) {
            future.complete(result);  // завершаем успешно
        }

        @Override
        public void onError(Exception e) {
            future.completeExceptionally(e);  // завершаем с ошибкой
        }
    });

    return future;
}
```

Оба метода возвращают `boolean`: `true`, если именно этот вызов завершил Future, и `false`, если он уже был завершён раньше. Завершить можно только один раз — повторные вызовы игнорируются (это удобно при гонке: например, рабочий результат против таймаута — кто успел первым, тот и зафиксировал исход).

---

## Q13. Когда НЕ стоит использовать CompletableFuture?

`CompletableFuture` рассчитан на **разовый** асинхронный результат с цепочкой трансформаций. Он начинает мешать там, где нужны потоки данных, отмена, повторы или backpressure — для этого есть специализированные инструменты. Грубое правило: если задач больше, чем «сделать N независимых вызовов и собрать результат», стоит посмотреть на альтернативу.

| Ситуация | Альтернатива |
|---|---|
| Реактивные стримы данных | Project Reactor (`Flux`/`Mono`) |
| Android / Kotlin | Coroutines |
| Простой однократный фоновый вызов | `@Async` Spring |
| Параллельные вычисления над коллекцией | `parallelStream()` |
| Нужен retry / backpressure | Reactor / RxJava |

`CompletableFuture` хорош для:
- Оркестрации нескольких независимых HTTP/DB вызовов
- Неблокирующих пайплайнов с трансформациями
- Адаптирования колбэков к Future-стилю

**Подводный камень:** по умолчанию работа идёт в `ForkJoinPool.commonPool()` — разделяемом на всю JVM пуле с малым числом потоков. При блокирующих IO-задачах заведите отдельный executor: иначе несколько блокировок исчерпают пул, и остальные `CompletableFuture` (и параллельные стримы) встанут в очередь.

---

## See also

- [Java Concurrency](java-concurrency-interview.md) — Future, ExecutorService, ForkJoinPool
- [Java 8](java-8-interview.md) — CompletableFuture как нововведение Java 8
- [Functional Interfaces](java-functional-interface-interview.md) — Function, Consumer, Supplier в CF
- [Java Stream API](java-stream-interview.md) — параллельные стримы vs CompletableFuture
- [Spring Scheduling](../../frameworks/spring/spring-scheduling-interview.md) — @Async и CompletableFuture в Spring
- [Reactive Streams](../../reactive/reactive-streams-interview.md) — Mono/Flux как альтернатива CompletableFuture
- [Java 17-21](java-17-21-interview.md) — Virtual Threads как альтернатива async-стилю
- [Spring WebFlux](../../frameworks/spring/spring-webflux-interview.md) — реактивная альтернатива для веб-запросов
