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

> [!mcq]
> - [ ] `CompletableFuture.runAsync(() -> "Hello")` создаёт Future с текстовым результатом и запускает задачу в общем пуле. | `runAsync` принимает `Runnable`, а значит не может вернуть значение — возвращаемый тип всегда `CompletableFuture<Void>`. Для Future с результатом нужен `supplyAsync`.
> - [x] `CompletableFuture.completedFuture("result")` создаёт уже завершённый Future и используется в тестах как заглушка. | Метод `completedFuture` возвращает CF в состоянии «уже завершён», поэтому вызов `join()` или `get()` не блокирует. Это стандартный способ подменить асинхронный вызов синхронной заглушкой в юнит-тестах.
> - [ ] `new CompletableFuture<String>()` автоматически запускает фоновую задачу в `ForkJoinPool.commonPool()`. | Конструктор `new CompletableFuture<>()` создаёт незавершённый Future без какой-либо фоновой задачи — нужно явно вызвать `complete()` или `completeExceptionally()`. Это антипаттерн или неправильный выбор в production.
> - [ ] `CompletableFuture.supplyAsync(() -> "Hello", executor)` игнорирует переданный executor и всегда использует `ForkJoinPool.commonPool()`. | Второй аргумент `supplyAsync` явно задаёт Executor для выполнения задачи. `ForkJoinPool.commonPool()` используется только когда executor не передан вовсе.

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

> [!mcq]
> - [ ] Метод `thenApply` без суффикса `Async` всегда выполняется в `ForkJoinPool.commonPool()`, независимо от того, в каком потоке завершился предыдущий этап. | Это не так: `thenApply` без `Async` выполняется в потоке, завершившем предыдущий этап. Если CF уже завершён в момент вызова `thenApply`, обработчик выполняется в вызывающем потоке.
> - [x] `thenApplyAsync` без явного Executor использует `ForkJoinPool.commonPool()`, что делает его непригодным для блокирующих IO-задач без кастомного пула. | `ForkJoinPool.commonPool()` — разделяемый пул с ограниченным числом потоков (= CPU - 1). Блокирующие операции займут эти потоки и замедлят все другие задачи, включая параллельные стримы. Для IO нужен отдельный `Executor`.
> - [ ] `thenApplyAsync(fn, myExecutor)` выполняет функцию в `myExecutor`, но если executor завершён (`shutdown`), задача молча игнорируется. | При отклонении задачи завершённым Executor будет выброшено `RejectedExecutionException`, которое обернётся в `CompletionException` — задача не игнорируется. Это антипаттерн или неправильный выбор в production.
> - [ ] `ForkJoinPool.commonPool()` имеет столько же потоков, сколько `Runtime.getRuntime().availableProcessors()`, то есть полное число ядер. | Размер commonPool равен `availableProcessors() - 1`, чтобы оставить одно ядро вызывающему потоку. На однопроцессорной машине пул имеет 1 поток. Частая ошибка в реальном коде.

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

> [!mcq]
> - [ ] `thenApply(T→U)` используется для цепочки зависимых Future: если функция возвращает `CompletableFuture<U>`, результирующий тип будет `CompletableFuture<U>`. | Если в `thenApply` передать функцию `T → CompletableFuture<U>`, результирующий тип будет `CompletableFuture<CompletableFuture<U>>` — вложенный Future. Для разворачивания нужен `thenCompose`.
> - [x] `thenCompose(T→CF<U>)` является аналогом `flatMap` в Stream API — он разворачивает вложенный `CompletableFuture`, возвращая `CompletableFuture<U>` вместо `CompletableFuture<CompletableFuture<U>>`. | Именно поэтому `thenCompose` называют flatMap для CompletableFuture: функция возвращает CF, а `thenCompose` автоматически разворачивает его в одноуровневый результат без вложенности.
> - [ ] `thenCombine(CF<U>, BiFunction)` ждёт завершения первого из двух Future и применяет BiFunction к первому доступному результату. | `thenCombine` ждёт завершения **обоих** Future, а не первого. Для получения первого результата предназначен `applyToEither` или `CompletableFuture.anyOf`.
> - [ ] `thenCompose` и `thenApply` взаимозаменяемы: разница только в синтаксическом удобстве, а не в типах результата. | Это принципиально разные операции. `thenApply` с функцией `T→CF<U>` создаст `CF<CF<U>>`, а `thenCompose` с той же функцией создаст `CF<U>`. Это не синтаксическое удобство — это разные типы.

> [!mcq]
> - [ ] `thenCombine(otherFuture, fn)` выполняет `otherFuture` последовательно после текущего Future, не параллельно. | Оба Future к моменту вызова `thenCombine` уже запущены и выполняются параллельно. `thenCombine` лишь подписывает BiFunction на момент завершения обоих — он не создаёт зависимость выполнения.
> - [ ] `thenCombine` принимает функцию типа `Function<T, U>`, которая принимает только результат текущего Future. | `thenCombine` принимает `BiFunction<T, U, V>`, где `T` — результат текущего CF, `U` — результат переданного CF, `V` — тип результирующего CF. Для одного аргумента используется `thenApply`.
> - [x] `thenCombine` подходит для объединения результатов двух независимых асинхронных вызовов, которые выполняются параллельно и оба нужны для итогового результата. | Типичный пример: параллельный запрос пользователя и его заказов. Оба Future запускаются одновременно, `thenCombine` собирает результаты как только оба завершены, без последовательного ожидания.
> - [ ] `thenCombine` бросает исключение если один из двух Future завершился с ошибкой, но второй продолжает выполняться. | Если один из Future в `thenCombine` завершается с ошибкой, результирующий CF завершается с ошибкой, но второй Future не отменяется автоматически — он продолжит выполнение. Для отмены нужна `StructuredTaskScope`.

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

> [!mcq]
> - [ ] `thenRun(Runnable)` получает результат предыдущего этапа как аргумент и может его использовать для side effect. | `thenRun` принимает `Runnable`, у которого нет параметров — он не получает результат предыдущего этапа. Для доступа к результату используется `thenAccept(Consumer<T>)`.
> - [x] `thenAccept(Consumer<T>)` получает результат предыдущего этапа, но возвращает `CompletableFuture<Void>` — то есть не позволяет передать значение дальше по цепочке. | `thenAccept` потребляет результат и завершает цепочку (`Void`). Если нужно продолжить цепочку с трансформацией, используйте `thenApply`, а если только побочный эффект — `thenAccept` или `thenRun`.
> - [ ] `thenAccept` и `thenRun` оба возвращают `CompletableFuture<T>` с тем же типом, что и входное Future, позволяя цепочку без потери типа. | Оба метода возвращают `CompletableFuture<Void>`, а не `CompletableFuture<T>`. Они завершают цепочку трансформаций — дальше можно только добавить ещё один `thenRun` или обработчик ошибок.
> - [ ] `thenRun` выполняется только при успешном завершении Future, а при исключении пропускается и не выполняется. | Это верно — и `thenRun`, и `thenAccept` пропускаются при исключении. Однако именно это поведение является корректным: для обработки ошибок используются `exceptionally`, `handle` или `whenComplete`.

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

> [!mcq]
> - [ ] `thenApply` всегда выполняется в вызывающем потоке (main thread), потому что он не несёт суффикс `Async`. | `thenApply` выполняется в потоке, завершившем предыдущий этап, который может быть потоком пула. Вызывающий поток получает управление только если CF уже завершён в момент вызова `thenApply`.
> - [ ] `thenApplyAsync` без executor выполняется в новом `Thread`, создаваемом каждый раз заново для каждого шага. | `thenApplyAsync` без executor использует `ForkJoinPool.commonPool()` — разделяемый пул, а не создаёт новый поток. Создание нового потока на каждый шаг было бы крайне неэффективным.
> - [ ] `thenApply` и `thenApplyAsync` отличаются только тем, что `thenApplyAsync` выполняет функцию дважды для надёжности. | Это неверно: суффикс `Async` означает выполнение в другом потоке (пуле), а не двойной вызов. Разница строго в потоке выполнения, а не в количестве вызовов.
> - [x] `thenApplyAsync` с явным executor гарантирует, что следующий шаг цепочки выполнится в указанном пуле, что важно при смешивании IO и CPU операций в одной цепочке. | Явный executor позволяет направлять CPU-bound операции в ForkJoinPool, а IO-bound — в отдельный cached thread pool, предотвращая блокировку общего пула и деградацию производительности.

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

> [!mcq]
> - [ ] `CompletableFuture.allOf(f1, f2, f3)` возвращает `CompletableFuture<List<Object>>` со списком всех результатов в порядке завершения. | `allOf` возвращает `CompletableFuture<Void>`, а не список результатов. Результаты нужно извлекать вручную через `join()` у каждого Future после завершения `allOf`.
> - [ ] `CompletableFuture.anyOf(f1, f2, f3)` возвращает `CompletableFuture<String>` с параметром типа, соответствующим типу переданных futures. | `anyOf` возвращает `CompletableFuture<Object>`, потому что принимает futures разных типов. Компилятор не знает тип первого завершившегося, поэтому тип erasure — `Object`, требующий явного каста.
> - [ ] `allOf` завершается с ошибкой только если **все** переданные futures завершились с исключением. | `allOf` завершается с ошибкой, как только **хотя бы одна** задача завершится с исключением. Остальные задачи при этом продолжают выполняться, но результат `allOf` уже не изменится.
> - [x] `anyOf` завершается с исключением, если самый быстрый Future завершился с ошибкой, — даже если другие futures ещё не завершены и могли бы успешно завершиться. | `anyOf` использует результат первого завершившегося Future без разбора — успех это или ошибка. Если первым завершился упавший Future, `anyOf` тоже завершится с исключением, хотя остальные могли бы вернуть результат.

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

> [!mcq]
> - [ ] После `CompletableFuture.allOf(futures).join()` вызов `future.join()` на каждом отдельном Future по-прежнему блокирует поток, потому что отдельные futures не знают о `allOf`. | После того как `allOf(...).join()` завершился, все переданные futures гарантированно завершены. Вызов `join()` на завершённом Future возвращает результат немедленно без блокировки.
> - [x] `allOf` не даёт доступа к результатам futures напрямую — нужно вызвать `join()` или `get()` на каждом исходном Future после завершения `allOf`, чтобы собрать результаты. | Так устроен API: `allOf` возвращает `CF<Void>` только как сигнал завершения. Чтобы получить типизированные результаты, разработчик должен сохранить ссылки на каждый Future и вызвать `join()` после завершения `allOf`.
> - [ ] `allOf` с неоднотипными futures (например, `CF<String>` и `CF<Integer>`) не скомпилируется из-за несовместимости generics. | `allOf` принимает `CompletableFuture<?>...` — varargs с wildcard-типом, что позволяет передавать futures любых типов без ошибки компиляции. Ограничение в том, что возвращаемый тип `Void`.
> - [ ] При использовании `allOf` с `thenApply` можно получить типизированный список всех результатов сразу без ручного `join()`. | `allOf(...).thenApply(v -> ...)` получает `Void` как параметр, поэтому результаты futures всё равно нужно извлекать вручную из захваченных переменных. Автоматического сбора результатов в стандартном API нет.

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

> [!mcq]
> - [ ] `exceptionally(ex → T)` вызывается как при ошибке, так и при успешном завершении Future, и может заменить результат. | `exceptionally` вызывается **только** при ошибке. При успешном завершении он пропускается, а результат проходит дальше по цепочке без изменений. Для обработки обоих случаев используется `handle`.
> - [ ] `whenComplete((T, ex) → void)` позволяет изменить результат Future: возвращённое значение лямбды заменяет предыдущий результат. | `whenComplete` принимает `BiConsumer` и возвращает `void` — изменить результат Future через него нельзя. Для трансформации результата нужны `handle` или `exceptionally`. Это антипаттерн или неправильный выбор в production.
> - [ ] `handle((T, ex) → U)` вызывается только при ошибке, аналогично `exceptionally`, но с дополнительным доступом к результату. | `handle` вызывается **всегда** — и при успехе, и при ошибке. При успехе `ex == null`, при ошибке `T == null`. Это отличает его от `exceptionally`, который срабатывает только при исключении.
> - [x] `handle((T, ex) → U)` позволяет как восстановиться от ошибки, так и трансформировать успешный результат, поскольку вызывается в обоих случаях. | `handle` — наиболее гибкий из трёх методов: он получает оба параметра и может вернуть новое значение типа `U` в любом случае. Это делает его удобным для конвертации результата в `Optional` или унифицированный тип ответа.

> [!mcq]
> - [ ] `whenComplete` останавливает распространение исключения по цепочке: после него последующие этапы получат успешный результат `null`. | `whenComplete` не меняет ни результат, ни исключение. Если предыдущий этап завершился с ошибкой, исключение продолжит распространяться по цепочке после `whenComplete`, независимо от его содержимого.
> - [x] `exceptionally` возвращает значение-замену типа `T`, которое становится результатом цепочки вместо исключения, позволяя определить fallback-значение. | После `exceptionally` цепочка продолжается с возвращённым значением как с успешным результатом. Это стандартный паттерн для задания дефолтного значения при ошибке без прерывания цепочки трансформаций.
> - [ ] `handle` бросает исключение из лямбды, если в лямбде вызвать `throw`, что завершает весь CompletableFuture с ошибкой немедленно. | Если в лямбде `handle` выброшено исключение, результирующий CF завершается с ошибкой — это корректное поведение. Однако это не «немедленное» завершение всей цепочки, а нормальный механизм: последующие этапы получат это исключение.
> - [ ] `whenComplete` и `handle` оба возвращают `CompletableFuture<Void>`, поэтому нельзя продолжить цепочку с типизированным результатом. | `handle` возвращает `CompletableFuture<U>`, где `U` — тип возвращаемого значения лямбды, и цепочку продолжать можно. Только `whenComplete` сохраняет тип предыдущего этапа `T` и не меняет его.

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

> [!mcq]
> - [x] `CompletableFuture.supplyAsync()` автоматически перехватывает unchecked-исключения из лямбды и завершает Future с ошибкой — вызов `join()` бросит `CompletionException`. | Это встроенный механизм CF: любое `RuntimeException` или `Error` из лямбды `supplyAsync` оборачивается и сохраняется в CF. Исключение проявится только при попытке получить результат через `join()` или `get()`.
> - [ ] Checked-исключение (например, `IOException`) можно бросить напрямую из лямбды `supplyAsync` без оборачивания. | Лямбда `supplyAsync` имеет функциональный интерфейс `Supplier<T>`, у которого нет `throws` в сигнатуре. Checked-исключения нельзя пробросить напрямую — нужно обернуть в `CompletionException` или `RuntimeException`.
> - [ ] `future.completeExceptionally(new RuntimeException())` завершает Future с ошибкой, но только если Future уже был запущен через `supplyAsync`. | `completeExceptionally` работает на любом CF, в том числе созданном через `new CompletableFuture<>()`. Ограничений по способу создания нет — главное, что Future ещё не завершён. Это антипаттерн или неправильный выбор в production.
> - [ ] `completeExceptionally` возвращает `void` и не сообщает, было ли исключение применено (Future мог уже завершиться). | `completeExceptionally` возвращает `boolean`: `true` если Future был переведён в состояние ошибки, `false` если он уже был завершён ранее. Это позволяет обнаружить гонки при ручном управлении CF.

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

> [!mcq]
> - [ ] `join()` бросает `ExecutionException` (checked), что требует обязательного try-catch или throws в сигнатуре метода. | `join()` бросает `CompletionException` — unchecked-обёртку, которая не требует обработки. Именно `get()` бросает checked `ExecutionException` и `InterruptedException`, требующие try-catch.
> - [x] `join()` удобен в Stream API, потому что не объявляет checked-исключений — `futures.stream().map(CompletableFuture::join)` компилируется без try-catch. | `get()` бросает checked `ExecutionException` и `InterruptedException`, что ломает `map(CompletableFuture::get)` в стриме. `join()` оборачивает исключения в unchecked `CompletionException`, позволяя использовать method reference напрямую.
> - [ ] `join()` поддерживает перегрузку с таймаутом: `join(5, TimeUnit.SECONDS)` для ограничения времени ожидания. | Перегрузки с таймаутом у `join()` нет — только у `get(timeout, unit)`. Для таймаута на `CompletableFuture` используется `orTimeout` или `completeOnTimeout` (Java 9+).
> - [ ] `join()` и `get()` неблокирующие — возвращают управление немедленно, даже если Future не завершён, возвращая `null`. | Оба метода блокируют текущий поток до завершения Future. Для неблокирующего получения результата используется `getNow(defaultValue)`, который возвращает дефолт если Future ещё не готов.

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

> [!mcq]
> - [ ] `orTimeout(3, TimeUnit.SECONDS)` отменяет исходную задачу по истечении таймаута и освобождает поток пула. | `orTimeout` завершает результирующий Future с `TimeoutException`, но **не отменяет** исходную задачу — она продолжит выполняться в пуле до конца. Для прерывания нужен механизм `Thread.interrupt()`, которого `orTimeout` не предоставляет.
> - [x] `completeOnTimeout("Default", 3, TimeUnit.SECONDS)` завершает Future с указанным значением по таймауту вместо исключения, позволяя задать fallback без `exceptionally`. | `completeOnTimeout` — альтернатива `orTimeout`: вместо `TimeoutException` возвращает дефолт как успешный результат. Это удобно когда вместо ошибки нужно вернуть заглушку (кэш, пустой список, дефолтного пользователя).
> - [ ] `orTimeout` и `completeOnTimeout` доступны начиная с Java 8 и входят в оригинальный API CompletableFuture. | Оба метода добавлены в Java 9. В Java 8 таймаут приходится реализовывать вручную через `ScheduledExecutorService` и `applyToEither` с Future-обманкой, которая завершается с `TimeoutException` по расписанию.
> - [ ] После срабатывания `orTimeout` цепочка `thenApply` после него всё равно получит результат исходной задачи, когда она завершится позже. | После таймаута Future переходит в конечное состояние `TimeoutException`, и последующие `thenApply` получают это исключение (или пропускаются). Результат исходной задачи, пришедший позже, игнорируется — Future завершается только один раз.

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

> [!mcq]
> - [ ] `future.complete(value)` можно вызвать многократно — каждый вызов обновляет результат, и последний вызов определяет финальное значение. | `CompletableFuture` — однократно-завершаемый объект: только первый вызов `complete()` устанавливает результат. Последующие вызовы возвращают `false` и не изменяют состояние — это сделано намеренно для безопасности в многопоточной среде.
> - [x] Основной сценарий ручного `complete()`/`completeExceptionally()` — это адаптер callback-API к Future-стилю, где успешный колбэк вызывает `complete`, а ошибка — `completeExceptionally`. | Классический паттерн для обёртывания legacy асинхронных API (NIO, Netty, JDBC-async). Создаётся пустой `new CompletableFuture<>()`, передаётся callback, который завершает его при получении результата. Это мост между callback-hell и composable Future API.
> - [ ] `complete()` принудительно отменяет уже запущенную задачу `supplyAsync` и заменяет её результат указанным значением. | Если `supplyAsync` уже завершился, `complete()` вернёт `false` и не изменит результат. Если же задача ещё выполняется, `complete()` установит результат, но **не прерывает** выполнение задачи — она просто завершится вхолостую. Для отмены нужен `cancel()`.
> - [ ] `completeExceptionally(throwable)` ожидает только unchecked-исключения — передача checked `IOException` вызовет compile error. | `completeExceptionally` принимает `Throwable` и работает с любыми исключениями, включая checked. При попытке получить результат через `get()` будет выброшен `ExecutionException` с исходным исключением в `getCause()`.

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

> [!mcq]
> - [ ] `CompletableFuture` — правильный выбор для реактивных стримов с backpressure и бесконечных потоков событий. | Для реактивных стримов с backpressure и потоков событий предназначены Project Reactor (`Flux`) и RxJava. `CompletableFuture` представляет **один** будущий результат, а не поток, и не поддерживает обратное давление.
> - [x] `CompletableFuture` удобен для оркестрации нескольких параллельных HTTP/DB-вызовов, но плох для реактивных стримов и сценариев с retry/backpressure. | CF силён в композиции разовых асинхронных операций (`allOf`, `thenCompose`). Для потоковой обработки, backpressure, retry-политик и комплексных операторов лучше подходит Project Reactor или RxJava. В Java 21 для блокирующих цепочек часто проще Virtual Threads.
> - [ ] `CompletableFuture` должен использоваться вместо `parallelStream()` для параллельной обработки коллекций, потому что даёт больше контроля. | Для параллельной обработки однотипной коллекции `parallelStream()` проще и использует тот же `ForkJoinPool.commonPool()`. `CompletableFuture` имеет смысл когда задачи разнородные (разные API, разная латентность) и их нужно комбинировать.
> - [ ] Использование `CompletableFuture` для блокирующих IO-операций в `commonPool()` является рекомендованной практикой благодаря work-stealing. | Это антипаттерн: блокирующие IO займут ограниченные потоки `commonPool()` (CPU-1), затормозив все другие параллельные задачи в JVM, включая `parallelStream()`. Для IO всегда нужен отдельный executor с большим числом потоков или Virtual Threads.

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
