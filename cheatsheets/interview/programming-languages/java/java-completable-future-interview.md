---
title: "Вопросы на собеседовании: Java CompletableFuture"
description: "CompletableFuture: thenApply/thenCompose/thenCombine, allOf/anyOf, обработка ошибок, timeout, ForkJoinPool, join() vs get()"
tags:
  - interview
  - programming-languages
  - java-completable-future-interview
aliases:
  - "Java CompletableFuture interview"
  - "CompletableFuture собеседование"
  - "CompletableFuture вопросы"
difficulty: "intermediate"
updated: "2026-04-20"
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
- [Q1. (!) Как создать CompletableFuture?](#q1-как-создать-completablefuture)
- [Q2. В каком потоке выполняется задача?](#q2-в-каком-потоке-выполняется-задача)

**Трансформация и цепочки**
- [Q3. (!) Чем thenApply, thenCompose и thenCombine отличаются?](#q3-чем-thenapply-thencompose-и-thencombine-отличаются)
- [Q4. Чем thenAccept отличается от thenRun?](#q4-чем-thenaccept-отличается-от-thenrun)
- [Q5. Что такое Async-суффикс у методов?](#q5-что-такое-async-суффикс-у-методов)

**Комбинирование нескольких Future**
- [Q6. (!) Чем allOf отличается от anyOf?](#q6-чем-allof-отличается-от-anyof)
- [Q7. Как получить результаты из allOf?](#q7-как-получить-результаты-из-allof)

**Обработка ошибок**
- [Q8. (!) Чем exceptionally, handle и whenComplete отличаются?](#q8-чем-exceptionally-handle-и-whencomplete-отличаются)
- [Q9. Как пробросить исключение в CompletableFuture?](#q9-как-пробросить-исключение-в-completablefuture)

**Блокирующие операции**
- [Q10. (!) Чем join() отличается от get()?](#q10-чем-join-отличается-от-get)
- [Q11. Как задать timeout для CompletableFuture?](#q11-как-задать-timeout-для-completablefuture)

**Продвинутые темы**
- [Q12. Что такое complete() и completeExceptionally()?](#q12-что-такое-complete-и-completeexceptionally)
- [Q13. Когда НЕ стоит использовать CompletableFuture?](#q13-когда-не-стоит-использовать-completablefuture)

---

## Q1. (!) Как создать CompletableFuture?

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

**Важно:** `ForkJoinPool.commonPool()` — разделяемый пул с `Runtime.availableProcessors() - 1` потоками. Для IO-задач нужен отдельный executor:

```java
ExecutorService ioExecutor = Executors.newCachedThreadPool();
CompletableFuture.supplyAsync(() -> httpClient.get(url), ioExecutor);
```

---

## Q3. (!) Чем thenApply, thenCompose и thenCombine отличаются?

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

Для каждого метода трансформации есть Async-версия с тем же именем + суффикс `Async`:
- `thenApply` → `thenApplyAsync`
- `thenAccept` → `thenAcceptAsync`
- `thenCompose` → `thenComposeAsync`

Без `Async` — следующий этап выполняется в том же потоке, что завершил предыдущий.
С `Async` — следующий этап выполняется в новом потоке (`ForkJoinPool` или указанный Executor).

```java
// thenApply: продолжение в том же потоке (может быть main thread если CF уже завершён)
future.thenApply(s -> transform(s));

// thenApplyAsync: всегда в пуле потоков
future.thenApplyAsync(s -> transform(s));
future.thenApplyAsync(s -> transform(s), myExecutor);
```

Используйте `Async` для CPU-интенсивных операций или чтобы не блокировать вызывающий поток.

---

## Q6. (!) Чем allOf отличается от anyOf?

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

**Исключение в anyOf:** если быстрее всего завершился исключением — `anyOf` тоже завершится исключением.

---

## Q7. Как получить результаты из allOf?

`allOf` возвращает `CompletableFuture<Void>` — результаты нужно извлечь отдельно через `join()` у каждого Future (после того как `allOf` завершился):

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

Оба **блокируют** текущий поток — использовать только там где блокировка допустима.

---

## Q11. Как задать timeout для CompletableFuture?

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

Позволяют вручную завершить `CompletableFuture` — полезно для адаптеров колбэков:

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

`complete()` и `completeExceptionally()` возвращают `boolean` — `true` если Future ещё не было завершено (можно завершить только один раз).

---

## Q13. Когда НЕ стоит использовать CompletableFuture?

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

**Ловушка:** `ForkJoinPool.commonPool()` — разделяемый пул. При блокирующих IO-задачах нужен отдельный executor, иначе все потоки пула займутся ожиданием и другие CompletableFuture будут ждать.

---

## See also

- [[java-concurrency-interview|Java Concurrency]] — Future, ExecutorService, ForkJoinPool
- [[java-8-interview|Java 8]] — CompletableFuture как нововведение Java 8
- [[java-functional-interface-interview|Functional Interfaces]] — Function, Consumer, Supplier в CF
- [[java-stream-interview|Java Stream API]] — параллельные стримы vs CompletableFuture
- [[spring-scheduling-interview|Spring Scheduling]] — @Async и CompletableFuture в Spring
- [[reactive-streams-interview|Reactive Streams]] — Mono/Flux как альтернатива CompletableFuture
- [[java-17-21-interview|Java 17-21]] — Virtual Threads как альтернатива async-стилю
- [[spring-webflux-interview|Spring WebFlux]] — реактивная альтернатива для веб-запросов
