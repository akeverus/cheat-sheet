---
title: "Вопросы на собеседовании: CompletableFuture в Java"
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
updated: "2026-11-05"
---
# Вопросы на собеседовании: CompletableFuture в Java

`CompletableFuture<T>` — реализация `Future` с поддержкой неблокирующих цепочек, комбинирования нескольких асинхронных операций и обработки ошибок. Введён в Java 8. На собеседованиях проверяют разницу между `thenApply`/`thenCompose`/`thenCombine`, обработку исключений и работу с пулом потоков.

Дата последнего обновления: 2026-10-07

## Полезные ссылки

### Официальная документация

- [CompletableFuture JavaDoc (Java 25)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CompletableFuture.html) — полный API
- [Future JavaDoc (Java 25)](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/Future.html) — контракт отмены задачи
- [Baeldung: CompletableFuture Guide](https://www.baeldung.com/java-completablefuture) — подробный туториал

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Создание и запуск**
- [Q1. (!) Как создать CompletableFuture?](#q1--как-создать-completablefuture)
- [Q2. В каком потоке выполняется задача?](#q2-в-каком-потоке-выполняется-задача)

**Трансформация и цепочки**
- [Q3. (!) Какое утверждение о thenApply, thenCompose и thenCombine верно?](#q3)
- [Q4. Чем thenAccept отличается от thenRun?](#q4-чем-thenaccept-отличается-от-thenrun)
- [Q5. Что такое Async-суффикс у методов?](#q5-что-такое-async-суффикс-у-методов)

**Комбинирование нескольких Future**
- [Q6. (!) Чем allOf отличается от anyOf?](#q6--чем-allof-отличается-от-anyof)
- [Q7. Как получить результаты из allOf?](#q7-как-получить-результаты-из-allof)

**Обработка ошибок**
- [Q8. (!) Какое утверждение об exceptionally, handle и whenComplete верно?](#q8)
- [Q9. Как пробросить исключение в CompletableFuture?](#q9-как-пробросить-исключение-в-completablefuture)

**Блокирующие операции**
- [Q10. (!) Чем join() отличается от get()?](#q10--чем-join-отличается-от-get)
- [Q11. Как задать timeout для CompletableFuture?](#q11-как-задать-timeout-для-completablefuture)

**Продвинутые темы**
- [Q12. Что такое complete() и completeExceptionally()?](#q12-что-такое-complete-и-completeexceptionally)
- [Q13. Когда НЕ стоит использовать CompletableFuture?](#q13-когда-не-стоит-использовать-completablefuture)
- [Q14. Что отменяет CompletableFuture.cancel(true) и как остановить исходную работу?](#q14-что-отменяет-completablefuturecanceltrue-и-как-остановить-исходную-работу)

---

## Q1. (!) Как создать CompletableFuture?

Для каждого независимого случая выберите способ получить `CompletableFuture`. Укажите, кто завершит его и будет ли у него значение.

```text
A. Строка уже есть в кеше.
B. Строку нужно вычислить на заданном Executor.
C. На заданном Executor нужно выполнить действие без возвращаемого значения.
D. Внешнее API позже вызовет один из двух колбэков: со строкой или с ошибкой.
```

- **A.** `completedFuture(cachedValue)` сразу возвращает успешно завершённый future со строкой.
- **B.** `supplyAsync(loadValue, executor)` передаёт вычисление исполнителю. Успешное завершение содержит значение, возвращённое `Supplier`.
- **C.** `runAsync(recordMetric, executor)` передаёт исполнителю `Runnable`. При успехе `CompletableFuture<Void>` завершается со значением `null`.
- **D.** Конструктор создаёт незавершённый future. Колбэки вызывают `complete(value)` или `completeExceptionally(error)`. Сам конструктор работу не запускает. Гонка нескольких завершений разобрана в Q12.

```java
// loadValue — Supplier<String>, recordMetric — Runnable.
CompletableFuture<String> cached = CompletableFuture.completedFuture(cachedValue);
CompletableFuture<String> loaded = CompletableFuture.supplyAsync(loadValue, executor);
CompletableFuture<Void> recorded = CompletableFuture.runAsync(recordMetric, executor);

CompletableFuture<String> callbackResult = new CompletableFuture<>();
externalApi.request(callbackResult::complete, callbackResult::completeExceptionally);
```

В Java 25 и 26 перегрузки `supplyAsync` и `runAsync` без `Executor` используют `ForkJoinPool.commonPool()`. Различия с Java 24 и влияние выбранного исполнителя разобраны в Q2 и Q5. [Java SE 26 — CompletableFuture](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html)

---

## Q2. В каком потоке выполняется задача?

У этапов **без** суффикса `Async` нет гарантированного потока: продолжение может выполнить поток, завершивший предыдущий этап, либо другой поток, вызвавший метод завершения. Если предыдущий этап уже завершён, продолжение может выполниться прямо при регистрации в вызывающем потоке. Вариант **с** `Async` планируется через исполнитель: в Java 25 и 26 обычный `CompletableFuture` без явного `Executor` использует `ForkJoinPool.commonPool()`. Подкласс может переопределить `defaultExecutor()` для нестатических методов. При явном аргументе применяется переданный исполнитель. Суффикс задаёт способ планирования, а не обещает отдельный поток для каждой задачи.

| Вариант | Поток выполнения |
|---|---|
| Без `Async` (`thenApply`, `thenAccept`, ...) | Поток завершения или другой поток, вызвавший завершение; если этап уже готов, возможен поток регистрации |
| `*Async` без `Executor` | В Java 25 и 26 — `ForkJoinPool.commonPool()` для обычного `CompletableFuture` |
| `*Async` с `Executor` | Указанный исполнитель |

```java
CompletableFuture.supplyAsync(() -> "data")
    .thenApply(s -> s.toUpperCase())
    .thenApplyAsync(s -> s + "!", executor)
    .thenAccept(System.out::println);
```

**Подводный камень:** `ForkJoinPool.commonPool()` разделяется между задачами приложения, в том числе параллельными стримами. Его параллелизм настраивается и не равен универсально числу процессоров минус один. Долгая блокирующая операция (HTTP, JDBC) может задержать другие задачи общего пула. Для такого ввода-вывода используйте подходящий управляемый исполнитель, размер и жизненный цикл которого контролирует приложение:

```java
CompletableFuture.supplyAsync(() -> httpClient.get(url), ioExecutor);
```

В Java 24 документация ещё описывала создание отдельного потока на задачу, если общий пул не поддерживал параллелизм хотя бы два. При миграции сверяйте правило с версией JDK. [Java 24](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/util/concurrent/CompletableFuture.html).

См. [`CompletableFuture` в Java SE 25](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CompletableFuture.html) и [`ForkJoinPool`](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/ForkJoinPool.html).

---

<a id="q3"></a>
## Q3. (!) Какое утверждение о thenApply, thenCompose и thenCombine верно?

Выберите метод для каждого независимого сценария и определите тип итогового этапа.

```text
A. Этап вернёт строку. Нужна её длина.
B. Этап вернёт пользователя. Только после получения его ID
   можно вызвать сервис заказов, который вернёт этап со списком заказов.
C. Загрузка пользователя и загрузка заказов уже запущены по известному ID.
   Для построения профиля нужны оба результата.
```


Методы различаются типом функции и числом входных этапов. `thenApply` преобразует значение функцией `T → U`. `thenCompose` принимает функцию `T → CompletionStage<U>` и разворачивает возвращённый этап; для функции, возвращающей `CompletableFuture`, это убирает вложенность `CF<CF<U>>`. `thenCombine` объединяет значения двух этапов после их нормального завершения. Пара `thenApply`/`thenCompose` похожа на `map`/`flatMap`.

| Метод | Аналогия | Назначение |
|---|---|---|
| `thenApply(T→U)` | `map` | Преобразовать результат |
| `thenCompose(T→CompletionStage<U>)` | `flatMap` | Цепочка зависимых этапов |
| `thenCombine(CompletionStage<U>, BiFunction)` | Объединение пары результатов | Объединить два этапа |

**A. thenApply — трансформация:**
```java
CompletableFuture<Integer> length = CompletableFuture
    .supplyAsync(() -> "hello")
    .thenApply(String::length);  // "hello" → 5
```

**B. thenCompose — цепочка зависимых вызовов (аналог flatMap):**
```java
// Сначала найти user по email, потом загрузить его заказы
CompletableFuture<List<Order>> orders = userService
    .findByEmail(email)               // CF<User>
    .thenCompose(user ->              // User → CF<List<Order>>
        orderService.findByUser(user.getId())  // возвращает CF
    );
// Без thenCompose получили бы CF<CF<List<Order>>> — вложенный Future
```

**C. thenCombine — результаты двух уже созданных задач:**
```java
CompletableFuture<User> userFuture = userService.fetchUser(id);
CompletableFuture<List<Order>> orderFuture = orderService.fetchOrders(id);

CompletableFuture<UserProfile> profile = userFuture
    .thenCombine(orderFuture,
        (user, orders) -> new UserProfile(user, orders)
    );
// при независимом запуске задачи могут выполняться параллельно
```

`thenCombine` не запускает задачи и не гарантирует их параллельного выполнения: он ждёт нормального завершения обоих этапов и применяет функцию к двум результатам. Если один этап завершится с ошибкой, объединяющая функция не будет вызвана. [Java SE 26 — CompletionStage](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletionStage.html)

---

## Q4. Чем thenAccept отличается от thenRun?

Какие аргументы получают обработчики и что возвращают зависимые этапы? Рассмотрите два независимых запуска: загрузка пользователя завершилась успешно, оба обработчика отработали без ошибок, либо сама загрузка завершилась исключением.

```java
CompletableFuture<User> loaded = loadUserAsync();
var cached = loaded.thenAccept(user -> userCache.put(user.getId(), user));
var reported = loaded.thenRun(() -> log.info("User loaded"));
```

`thenAccept` передаёт загруженного пользователя в `Consumer`. Лямбда вызывает `put` ради обновления кеша, а возвращённое самим `put` значение отбрасывается. `thenRun` вызывает `Runnable` без аргументов. Для сообщения достаточно знать, что загрузка завершилась успешно.

Оба метода возвращают `CompletableFuture<Void>`. При успешной загрузке и нормальном завершении соответствующего действия результат зависимого этапа — `null`. Если загрузка завершилась исключением, оба действия пропускаются и оба зависимых этапа завершаются исключением. Этапы в примере независимо подписаны на `loaded`, поэтому сообщение о загрузке может появиться до записи в кеш. Если сообщение должно следовать за успешной записью, следующий этап нужно присоединить к `cached`.

Для дальнейшего преобразования пользователя с возвращаемым значением подходит `thenApply` (Q3). [Java SE 26 — CompletionStage](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletionStage.html)

---

## Q5. Что такое Async-суффикс у методов?

Суффикс `Async` задаёт способ планирования этапа через исполнитель. У многих методов `CompletionStage` есть обычный и `*Async`-вариант.
- `thenApply` → `thenApplyAsync`
- `thenAccept` → `thenAcceptAsync`
- `thenCompose` → `thenComposeAsync`

Правило для обычного `CompletableFuture`:
- **Без `Async`** — этап может выполнить поток, завершивший предыдущий этап, или другой поток, вызвавший метод завершения. Если Future уже готов, продолжение может выполниться при регистрации в вызывающем потоке.
- **С `Async`** — этап планируется через исполнитель по умолчанию или переданный `Executor`. Это не обещает новый поток для каждого этапа.

```java
CompletableFuture<String> source = CompletableFuture.completedFuture("report");
Executor inline = Runnable::run;
CompletableFuture<String> result = source.thenApplyAsync(String::toUpperCase, inline);
```

**Что происходит в примере.** Исходный этап уже готов, а `inline.execute(...)` вызывает `Runnable.run()` прямо в потоке регистрации. Поэтому даже продолжение с `Async` здесь выполняется в этом потоке. [Контракт Executor](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/Executor.html).

**Когда брать `Async`.** Если продолжение должно выполняться отдельно от потока завершения, выберите исполнитель, который передаёт задачу рабочему потоку, и проверьте его политику при перегрузке. Для короткого продолжения обычная версия может быть проще, если допустимо его выполнение в потоке завершения или регистрации. Планирование через исполнитель имеет стоимость.

---

## Q6. (!) Чем allOf отличается от anyOf?

В Java 26 два запроса представлены futures. Оба агрегата создаются до их завершения. Каким будет состояние каждого агрегата в точках A и B?

```java
CompletableFuture<String> primary = new CompletableFuture<>();
CompletableFuture<String> replica = new CompletableFuture<>();

CompletableFuture<Void> all = CompletableFuture.allOf(primary, replica);
CompletableFuture<Object> any = CompletableFuture.anyOf(primary, replica);

primary.completeExceptionally(new IllegalStateException("primary failed"));
// A
replica.complete("report");
// B
```

**Разбор.** В точке A `any` уже завершился исключением от `primary`, а `all` ещё ждёт `replica`. В точке B `all` тоже завершается исключением, поскольку один из входов завершился с ошибкой. Успех `replica` не меняет исход `any`. Наблюдать ошибку можно через `join()`, который в этом примере выбрасывает `CompletionException` с исходной причиной.

| | `allOf` | `anyOf` |
|---|---|---|
| Условие завершения | Завершились все входы, включая завершившиеся с ошибкой | Достаточно завершения одного входа, включая ошибку |
| Тип | `CompletableFuture<Void>` | `CompletableFuture<Object>` |
| Результат при успехе | `null`. Значения читают из исходных futures, как в Q7 | Значение входа, завершение которого определило исход |

Если несколько входов уже завершены или завершаются конкурентно, контракт `anyOf` не задаёт порядок выбора между ними. Если сервису нужен **первый успешный** ответ, он должен отдельно обработать исход каждой ветки и завершить общий результат ошибкой только после исчерпания всех вариантов. Оба агрегата оставляют управление исходными задачами вызывающему коду. Для остановки проигравшей работы нужен отдельный механизм, как в Q14.

При пустом наборе `allOf()` возвращает этап, уже завершившийся с `null`, а `anyOf()` возвращает незавершённый этап. [Java 26 `allOf`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html#allOf(java.util.concurrent.CompletableFuture...)) и [`anyOf`](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html#anyOf(java.util.concurrent.CompletableFuture...)).


---

## Q7. Как получить результаты из allOf?

Для панели уже запущены три запроса. Как дождаться их общего успеха и собрать результат с исходными типами? Как изменится сборка, если профиль и данные счёта обязательны, а при ошибке загрузки заказов разрешён пустой список?

```java
CompletableFuture<User> userFuture = loadUser(id);
CompletableFuture<List<Order>> orderFuture = loadOrders(id);
CompletableFuture<AccountInfo> accountFuture = loadAccount(id);
```

`allOf` возвращает `CompletableFuture<Void>`, то есть сам по себе он лишь сигнал «все готовы», а не контейнер с результатами. Поэтому шаблон такой: дождаться `allOf(...).join()`, а затем вызвать `join()` у каждого исходного Future. Ключевой момент — если `allOf(...).join()` вернулся нормально, все исходные futures уже завершились успешно. Последующие `join()` сразу возвращают готовые значения.

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

CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();

List<String> results = futures.stream()
    .map(CompletableFuture::join)  // уже завершены, нет блокировки
    .collect(Collectors.toList());
```

**Частичный результат.** Для той же панели обработайте ошибку ветки заказов до передачи её в агрегат. Профиль и данные счёта по-прежнему нужны для сборки.

```java
CompletableFuture<List<Order>> ordersOrEmpty =
    orderFuture.exceptionally(error -> List.of());

CompletableFuture<UserDashboard> dashboardWithFallback =
    CompletableFuture.allOf(userFuture, ordersOrEmpty, accountFuture)
        .thenApply(ignored -> new UserDashboard(
            userFuture.join(), ordersOrEmpty.join(), accountFuture.join()));
```

`thenApply` собирает данные после успешного завершения агрегата. Внутренние `join()` в этот момент читают три готовых результата. Если профиль или данные счёта загрузились с ошибкой, агрегат завершится исключением после завершения всех входов и сборка будет пропущена. Если после восстановления прочитать исходный `orderFuture`, его `join()` по-прежнему выбросит исключение. Если клиенту важно отличить пустой результат от недоступности сервиса, сохраните признак сбоя рядом со списком. [Java SE 26 — allOf](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html#allOf(java.util.concurrent.CompletableFuture...))


---

<a id="q8"></a>
## Q8. (!) Какое утверждение об exceptionally, handle и whenComplete верно?

В каждом независимом запуске зарегистрируйте три обработчика на одном `source`, затем выполните одно из двух завершений в конце примера. Какие обработчики вызовутся и чем завершится каждый зависимый этап?

```java
CompletableFuture<String> source = new CompletableFuture<>();

var a = source.exceptionally(error -> "guest");
var b = source.handle((name, error) -> error == null ? name.length() : -1);
var c = source.whenComplete((name, error) -> {
    throw new IllegalStateException("audit failed");
});

// Первый запуск:
source.complete("Nina");

// Второй запуск с новым source и теми же обработчиками:
// source.completeExceptionally(new IllegalArgumentException("lookup failed"));
```

| Метод | Успешный источник (`"Nina"`) | Ошибка источника (`"lookup failed"`) |
|---|---|---|
| `exceptionally` | Функция пропускается, `a` получает `"Nina"` | Функция вызывается, `a` получает `"guest"` |
| `handle` | Функция вызывается, `b` получает `4` | Функция вызывается, `b` получает `-1` |
| `whenComplete` | Действие вызывается, `c` завершается с ошибкой `"audit failed"` | Действие вызывается, для `c` приоритет имеет исходная ошибка `"lookup failed"` |

`exceptionally` восстанавливает результат того же типа после ошибки. `handle` получает результат и исключение и может вернуть значение другого типа, здесь `Integer`. Если успешный результат может быть `null`, для различения исходов проверяйте аргумент с исключением, а для использования значения обработайте `null` отдельно.

`whenComplete` подходит для логирования и метрик. Если действие завершится нормально, зависимый этап сохранит исходный результат или исходную ошибку. Если действие выбросит исключение, оно меняет успешный исход зависимого этапа. При двух ошибках контракт отводит приоритет ошибке источника. Все три продолжения зависят непосредственно от `source`, поэтому ошибка обработчика `c` не меняет `a` и `b`. [Java SE 26 — CompletionStage](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletionStage.html)

---

## Q9. Как пробросить исключение в CompletableFuture?

Метод `Files.readString` может выбросить `IOException`. Скомпилируется ли прямой вызов внутри `supplyAsync` и как передать ошибку вызывающему коду через future?

```java
Path path = Path.of("report.txt");
CompletableFuture<String> report =
    CompletableFuture.supplyAsync(() -> Files.readString(path));
```

**Разбор.** Лямбда должна соответствовать `Supplier<String>`, а его метод `get()` не объявляет `IOException`. Перехватите проверяемое исключение внутри лямбды и сохраните его как причину `CompletionException`. Если задача сама выбрасывает непроверяемое исключение, например `IllegalStateException`, `supplyAsync` и без такого перехвата завершит future с ошибкой. [JLS §11.2.3](https://docs.oracle.com/javase/specs/jls/se26/html/jls-11.html#jls-11.2.3).

```java
CompletableFuture<String> report = CompletableFuture.supplyAsync(() -> {
    try {
        return Files.readString(path);
    } catch (IOException e) {
        throw new CompletionException(e);
    }
});
```

Если future создаёт адаптер callback API, он может передать полученную ошибку напрямую через `report.completeExceptionally(error)`. Для ошибки чтения в примере `join()` выбросит `CompletionException`, а `get()` — `ExecutionException`. У обеих обёрток причиной будет исходный `IOException`. При прямой отмене самого future оба метода выбрасывают `CancellationException`, как указано в Q10. [Java 26 CompletableFuture](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html).


---

## Q10. (!) Чем join() отличается от get()?

Future отчёта ещё не завершён, и до конца вызова A никто его не меняет. Поток, вызвавший A, не прерывают. Вызовы A и B рассматриваются по отдельности: между ними поставщик завершает тот же future строкой `"report"`. Что получит каждый вызывающий?

```java
static String readA(CompletableFuture<String> report)
        throws InterruptedException, ExecutionException, TimeoutException {
    return report.get(100, TimeUnit.MILLISECONDS);
}

static String readB(CompletableFuture<String> report) {
    return report.join();
}
```

**Разбор.** Вызов A завершится с `TimeoutException`. Поставщик по-прежнему может записать результат в исходный future. Вызов B, сделанный после успешного завершения, сразу вернёт `"report"`.

Если результат ещё не готов, оба метода ждут его и могут блокировать текущий поток. `join()` при ошибке вычисления бросает **unchecked** `CompletionException` и не объявляет проверяемых исключений — поэтому его удобно вызывать прямо в лямбдах Stream API. `get()` — метод из интерфейса `Future`: при ошибке он бросает **checked** `ExecutionException`, а при прерывании ожидания — `InterruptedException`. У `get()` есть перегрузка с таймаутом `get(timeout, unit)`; у `join()` такой перегрузки нет. Таймаут `get(timeout, unit)` завершает только ожидание вызывающего потока. Сам future может позже завершиться своим результатом. Для таймаута самого этапа в Java 9+ также есть `orTimeout` и `completeOnTimeout` — это другая операция.

| | `join()` | `get()` |
|---|---|---|
| Исключение | `CompletionException` (unchecked) | `ExecutionException` (checked), `InterruptedException` |
| Использование в Stream | Ссылка на метод допустима в `Stream.map` | Проверяемые исключения нужно обработать в лямбде |
| Ограничение ожидания | Без параметра таймаута | `get(timeout, unit)` |
| Прямая отмена этого future | `CancellationException` | `CancellationException` |

**Главное:** на незавершённом Future оба метода **могут блокировать** текущий поток. Вызывайте их там, где такое ожидание допустимо (на границе пайплайна, в тестах); при уже готовом результате ждать не приходится. См. [`CompletableFuture` в Java SE 26](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html).

---

## Q11. Как задать timeout для CompletableFuture?

```java
// Java 9+. Источник вернёт имя через 5 секунд.
// API ждёт до 3 секунд, а отчёт должен получить полное имя.
// Что произойдёт с report, если дедлайн задать на shared?
// Как задать дедлайн только для API?
CompletableFuture<String> shared = new CompletableFuture<>();
scheduler.schedule(() -> shared.complete("Ada"), 5, TimeUnit.SECONDS);
CompletableFuture<String> report = shared.thenApply(name -> "User: " + name);
```

Начиная с Java 9 есть два штатных метода: `orTimeout` (завершить future с `TimeoutException`, если не успели) и `completeOnTimeout` (подставить значение по таймауту). Оба возвращают **тот же** `CompletableFuture`: если его используют и другие потребители, они тоже увидят завершение по таймауту. В Java 8 этих методов нет; исход можно собрать в отдельном `CompletableFuture` и завершить его из рабочего future либо по таймеру.

Если трёхсекундный таймер сработает первым, `shared.orTimeout(3, TimeUnit.SECONDS)` завершит общий future с `TimeoutException`. Функция отчёта пропустится, и `report` тоже завершится с ошибкой. Для отдельного дедлайна API используйте `shared.copy().orTimeout(3, TimeUnit.SECONDS)`. Таймер завершит копию, а отчёт дождётся имени через исходный future. [Javadoc CompletableFuture](https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/concurrent/CompletableFuture.html#copy()).

Таймаут меняет **результат future**, но сам по себе не прерывает вычисление в пуле; оно может продолжиться, а поздний результат уже не изменит завершённый future. См. Q14 об отдельной отмене работы.

**Java 9+:**
```java
// orTimeout — завершается TimeoutException если не готово вовремя
CompletableFuture<String> withTimeout = fetchUser(id)
    .orTimeout(3, TimeUnit.SECONDS);
// join() после таймаута бросит CompletionException с причиной TimeoutException

// completeOnTimeout — возвращает дефолт при таймауте
CompletableFuture<String> withDefault = fetchUser(id)
    .completeOnTimeout("DefaultUser", 3, TimeUnit.SECONDS);
```

**Java 8 (workaround):**
```java
public static <T> CompletableFuture<T> withTimeout(
        CompletableFuture<T> source, ScheduledExecutorService scheduler,
        long timeout, TimeUnit unit) {
    CompletableFuture<T> result = new CompletableFuture<>();
    ScheduledFuture<?> alarm = scheduler.schedule(
        () -> result.completeExceptionally(new TimeoutException()), timeout, unit);
    source.whenComplete((value, error) -> {
        if (error == null) result.complete(value);
        else result.completeExceptionally(error);
        alarm.cancel(false);
    });
    return result;
}
```

Планировщиком владеет вызывающий код. При остановке приложения он завершает и планировщик.

Функция в `applyToEither` получает значение при нормальном завершении. Если один вход завершился исключением, а другой успешно, контракт `CompletionStage` допускает и успешный, и исключительный итог зависимого этапа. Поэтому `applyToEither` не выражает правило «если первым завершился таймер, клиент получает `TimeoutException`». В примере обе ветки явно конкурируют за завершение `result`. Если первым сработал таймер, поздний успех источника уже не изменит итог этого этапа. [Java 8 CompletionStage](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html) · [Java 8 CompletableFuture](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html).

---

## Q12. Что такое complete() и completeExceptionally()?

Что вернут два вызова завершения и чем закончится `join()`? Во втором независимом запуске поменяйте порядок вызовов `complete` и `completeExceptionally`, сохранив `join()` последним.

```java
CompletableFuture<String> reply = new CompletableFuture<>();
boolean response = reply.complete("payload");
boolean failure = reply.completeExceptionally(new IOException("connection closed"));
String result = reply.join();
```

В первом запуске `response == true`, `failure == false`, а `join()` возвращает `"payload"`. Во втором `failure == true`, `response == false`, а `join()` выбрасывает `CompletionException` с причиной `IOException`. При конкурентных вызовах итог зависит от того, какая попытка первой завершит future. [Java SE 26 — CompletableFuture](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html)


Это методы, которыми вы **сами** завершаете Future извне: `complete(value)` переводит его в успешное состояние, `completeExceptionally(ex)` — в ошибочное. Интерфейс `Future` предоставляет ожидание, проверку состояния и отмену. Методы ручного завершения добавляет `CompletableFuture`. Главный сценарий — мост между callback-API (старые клиенты, JMS, низкоуровневый NIO) и Future-стилем: создаём пустой Future, отдаём его наружу, а в колбэках `onSuccess`/`onError` завершаем нужным методом.

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

Оба метода возвращают `boolean`: `true`, если именно этот вызов завершил Future, и `false`, если он уже был завершён раньше. У `obtrudeValue` и `obtrudeException` отдельный контракт: они принудительно меняют исход для последующих чтений даже у завершённого future. Продолжения, которые уже получили старый исход, могут сохранить его.

**Кто владеет завершением.** Если адаптер отдаёт свой `CompletableFuture` напрямую, вызывающий код может первым завершить его вручную или отменить. Тогда обработчик клиента уже не сможет записать рабочий результат. Если владельцем завершения должен остаться адаптер, в Java 9+ можно объявить возвращаемый тип `CompletionStage<String>` и вернуть `future.minimalCompletionStage()`. Вызывающий код сможет строить продолжения. Простая смена возвращаемого типа при `return future` сохраняет доступ к тому же объекту через `toCompletableFuture()`.

Если вызывающему коду нужен именно `CompletableFuture<String>`, верните `future.copy()`. Это отдельный future: вызывающий код может завершить или отменить свою копию, а исходный future по-прежнему завершит адаптер. Например, `future.copy().orTimeout(1, TimeUnit.SECONDS)` ограничивает ожидание этого клиента, сохраняя исходный результат для других продолжений. Остановка самой операции требует отдельного механизма (Q14). [Java 26: minimalCompletionStage](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html#minimalCompletionStage()) · [Java 26: copy](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html#copy()).

---

## Q13. Когда НЕ стоит использовать CompletableFuture?

Какую абстракцию выбрать для каждой из двух операций и кто управляет темпом доставки?

```text
A. API один раз возвращает собранный профиль пользователя или ошибку.
B. API выдаёт последовательность событий. Потребитель обрабатывает их
   порциями до 100 и запрашивает новую порцию после обработки текущей.
```

В A подходит `CompletableFuture<UserProfile>`. Он представляет один итог операции. Из него можно строить цепочки, объединять результаты нескольких запросов или адаптировать API с колбэками.

В B нужен потоковый API с управлением спросом, например `Flow.Publisher<Event>` (Java 9+) или Reactor `Flux<Event>`. В `Flow` подписчик запрашивает до 100 элементов через `request(100)`, а после их обработки добавляет следующий запрос к спросу. Источник может завершить поток раньше. Отмена подписки прекращает доставку по мере обработки сигнала, поэтому несколько событий ещё могут прийти после `cancel()`. [Java SE 26 — Flow.Subscription](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/Flow.Subscription.html)

`CompletableFuture<List<Event>>` выдаёт один список целиком. В цепочке таких future можно вручную запрашивать страницы по очереди, если источник предоставляет API пагинации. Управление спросом и остановку источника в этом случае реализует вызывающий код. Граница отмены самого future разобрана в Q14.

Выбор абстракции для результата и выбор исполнителя решают разные задачи. Для разового запроса с длительным блокирующим вводом-выводом занятые потоки общего пула могут задержать другие задачи. Выбор между отдельным исполнителем и неблокирующим клиентом зависит от клиента и нагрузки. Правила выбора исполнителя для фабрик и продолжений см. в Q2 и Q5. [Java SE 26 — CompletableFuture](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html)

---

## Q14. Что отменяет CompletableFuture.cancel(true) и как остановить исходную работу?

В двух независимых запусках операция уже ждёт в прерываемом вызове. Какой результат получит клиент? В каком запуске рабочий поток получит запрос на прерывание?

```text
В каждом запуске ThreadPoolExecutor.submit принимает новую задачу и возвращает task.
Задача записывает итог в отдельный CompletableFuture<Result> result.
После вызовов отмены клиент вызывает result.join().

A. Клиент вызывает только result.cancel(true).
B. Клиент вызывает result.cancel(true), затем task.cancel(true).
```

В обоих запусках `result.join()` выбрасывает `CancellationException`. В A уже начатое ожидание задачи продолжается. В B отмена `task` запрашивает прерывание рабочего потока. Реакция задачи зависит от того, как она обрабатывает прерывание.

`CompletableFuture.cancel(true)` завершает **сам future** с `CancellationException` и передаёт отмену незавершённым зависимым этапам как ошибочное завершение. Параметр `mayInterruptIfRunning` здесь **не действует**: JavaDoc прямо говорит, что прерывания не используются для управления вычислением. То же различие важно для `orTimeout` из Q11 — завершение future не означает остановку его источника.

Если нужно остановить работу, надо владеть **механизмом запуска**: например, сохранить `Future<?> task` от `ExecutorService.submit` и вызвать `task.cancel(true)`. В таком `Future` флаг разрешает попытку прервать исполняющий поток, но задача должна корректно реагировать на прерывание. Для HTTP/БД нужны timeout и поддерживаемый клиентом механизм отмены; прерывание потока само по себе не гарантирует остановку внешнего вызова. Если операция уже выполнила внешний эффект, отмена локального future его не откатит.

```java
CompletableFuture<Result> result = new CompletableFuture<>();
Future<?> task = executor.submit(() -> {
    try {
        result.complete(doInterruptibleWork());
    } catch (Exception error) {
        result.completeExceptionally(error);
    }
});

// При отмене запроса применяем оба действия явно.
if (result.cancel(false)) {
    task.cancel(true); // просим executor прервать исходную задачу
}
```

Этот пример предполагает, что `doInterruptibleWork()` поддерживает прерывание; для произвольного `supplyAsync` отдельный `Future` задачи обычно недоступен. См. [CompletableFuture.cancel](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/CompletableFuture.html#cancel(boolean)) и [Future.cancel](https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/util/concurrent/Future.html#cancel(boolean)) (Java 26).

## See also

- [Java Concurrency](java-concurrency-interview.md) — Future, ExecutorService, ForkJoinPool
- [Java 8](java-8-interview.md) — CompletableFuture как нововведение Java 8
- [Functional Interfaces](java-functional-interface-interview.md) — Function, Consumer, Supplier в CF
- [Java Stream API](java-stream-interview.md) — параллельные стримы vs CompletableFuture
- [Spring Scheduling](../../frameworks/spring/spring-scheduling-interview.md) — @Async и CompletableFuture в Spring
- [Reactive Streams](../../reactive/reactive-streams-interview.md) — Mono/Flux как альтернатива CompletableFuture
- [Java 17-21](java-17-21-interview.md) — Virtual Threads как альтернатива async-стилю
- [Spring WebFlux](../../frameworks/spring/spring-webflux-interview.md) — реактивная альтернатива для веб-запросов
