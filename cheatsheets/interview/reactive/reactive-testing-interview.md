---
title: "Вопросы на собеседовании: Тестирование реактивного кода"
description: "Тестирование реактивного кода: StepVerifier, TestPublisher, виртуальное время, WebTestClient, тестирование backpressure"
tags:
  - interview
  - reactive
  - reactive-testing-interview
  - testing
  - stepverifier
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Тестирование реактивного кода"
  - "Reactive Testing interview"
  - "StepVerifier interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: Тестирование реактивного кода

Гид по вопросам тестирования реактивного кода для `Senior Java Developer`. Охватывает `StepVerifier`, `TestPublisher`, виртуальное время (`VirtualTimeScheduler`), `WebTestClient`, тестирование backpressure и распространённые ошибки при написании реактивных тестов.

**Тестирование реактивных цепочек** — нетривиальная задача: нельзя просто вызвать метод и проверить результат, нужно подписаться на поток и верифицировать события. `Project Reactor` предоставляет `reactor-test` — специальную библиотеку с `StepVerifier` и `TestPublisher`.

## Полезные ссылки

### Официальная документация

- [Reactor Test Reference](https://projectreactor.io/docs/core/release/reference/#testing) — официальная документация по тестированию
- [StepVerifier Javadoc](https://projectreactor.io/docs/test/release/api/) — API `StepVerifier`

### Статьи

- [Testing Reactive Streams with StepVerifier and TestPublisher](https://www.baeldung.com/reactive-streams-step-verifier-test-publisher) — Baeldung
- [Guide to WebTestClient](https://www.baeldung.com/spring-5-webclient) — тестирование HTTP

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**StepVerifier — основы**
- [Q1. Что такое StepVerifier и как он работает?](#q1-что-такое-stepverifier-и-как-он-работает)
- [Q2. Как проверить элементы Flux через StepVerifier?](#q2-как-проверить-элементы-flux-через-stepverifier)
- [Q3. Как проверить, что Mono завершился ошибкой?](#q3-как-проверить-что-mono-завершился-ошибкой)
- [Q4. Как проверить, что поток пустой (empty)?](#q4-как-проверить-что-поток-пустой-empty)
- [Q5. Что делает verifyComplete() vs verify()?](#q5-что-делает-verifycomplete-vs-verify)

**StepVerifier — продвинутые сценарии**
- [Q6. Как проверить элементы с помощью expectNextMatches?](#q6-как-проверить-элементы-с-помощью-expectnextmatches)
- [Q7. Как проверить N элементов через expectNextCount?](#q7-как-проверить-n-элементов-через-expectnextcount)
- [Q8. Как использовать assertNext для сложных проверок?](#q8-как-использовать-assertnext-для-сложных-проверок)
- [Q9. Как тестировать сигналы с помощью recordWith?](#q9-как-тестировать-сигналы-с-помощью-recordwith)
- [Q10. Как тестировать Hot publishers через StepVerifier?](#q10-как-тестировать-hot-publishers-через-stepverifier)

**Виртуальное время**
- [Q11. Что такое виртуальное время (Virtual Time) и зачем оно нужно?](#q11-что-такое-виртуальное-время-virtual-time-и-зачем-оно-нужно)
- [Q12. Как использовать StepVerifier.withVirtualTime?](#q12-как-использовать-stepverifierwithvirtualtime)
- [Q13. Как тестировать delayElements и interval?](#q13-как-тестировать-delayelements-и-interval)
- [Q14. Как тестировать timeout с виртуальным временем?](#q14-как-тестировать-timeout-с-виртуальным-временем)

**TestPublisher**
- [Q15. Что такое TestPublisher и когда его использовать?](#q15-что-такое-testpublisher-и-когда-его-использовать)
- [Q16. Как эмитировать элементы и ошибки через TestPublisher?](#q16-как-эмитировать-элементы-и-ошибки-через-testpublisher)
- [Q17. Как тестировать backpressure через TestPublisher?](#q17-как-тестировать-backpressure-через-testpublisher)
- [Q18. Как создать TestPublisher без соблюдения спецификации Reactive Streams?](#q18-как-создать-testpublisher-без-соблюдения-спецификации-reactive-streams)

**WebTestClient**
- [Q19. Что такое WebTestClient?](#q19-что-такое-webtestclient)
- [Q20. Как настроить WebTestClient для тестирования WebFlux-контроллера?](#q20-как-настроить-webtestclient-для-тестирования-webflux-контроллера)
- [Q21. Как проверить тело ответа через WebTestClient?](#q21-как-проверить-тело-ответа-через-webtestclient)
- [Q22. Как тестировать SSE через WebTestClient?](#q22-как-тестировать-sse-через-webtestclient)

**Тестирование backpressure**
- [Q23. Как проверить, что publisher соблюдает backpressure?](#q23-как-проверить-что-publisher-соблюдает-backpressure)
- [Q24. Как тестировать onBackpressureDrop через StepVerifier?](#q24-как-тестировать-onbackpressuredrop-через-stepverifier)

**Распространённые ошибки**
- [Q25. Почему нельзя вызывать block() в тестах Spring WebFlux?](#q25-почему-нельзя-вызывать-block-в-тестах-spring-webflux)
- [Q26. Что произойдёт, если не вызвать verify() / verifyComplete()?](#q26-что-произойдёт-если-не-вызвать-verify--verifycomplete)
- [Q27. Как тестировать реактивные методы с @Transactional?](#q27-как-тестировать-реактивные-методы-с-transactional)
- [Q28. Как правильно замокать реактивные зависимости через Mockito?](#q28-как-правильно-замокать-реактивные-зависимости-через-mockito)

---

## Q1. Что такое StepVerifier и как он работает?

`StepVerifier` — главный инструмент тестирования реактивного кода из `reactor-test`. Он позволяет описать **сценарий ожидаемых сигналов** декларативно, шаг за шагом, и проверить, что поток действительно их выдаёт.

Почему он нужен: реактивную цепочку нельзя протестировать обычным `assertEquals` — это не значение, а поток событий во времени. `StepVerifier` подписывается на publisher, записывает приходящие сигналы (`onNext`, `onError`, `onComplete`) и сравнивает фактическую последовательность с ожидаемой.

**Три шага сценария:**
1. `StepVerifier.create(publisher)` — оборачивает publisher (но ещё не подписывается — цепочка ленивая).
2. Цепочка `expectNext*` / `expectError*` / `expectComplete` — описывает, какие сигналы и в каком порядке должны прийти.
3. `.verify()` или `.verifyComplete()` — **триггер**: подписывается, прогоняет поток и блокирует тест до завершения. Без этого вызова проверка не запустится.

```java
// Зависимость: io.projectreactor:reactor-test (test scope)

Mono<String> greeting = Mono.just("Hello, Reactor!");

StepVerifier.create(greeting)
        .expectNext("Hello, Reactor!")
        .verifyComplete();
```

## Q2. Как проверить элементы Flux через StepVerifier?

`expectNext(...)` сверяет элементы **строго по порядку** — каждый ожидаемый элемент должен совпасть со следующим `onNext`. Можно перечислить элементы по одному или несколько в одном вызове. Преобразования (`map`, `filter`) применяются к самому publisher до `create`, а проверяется уже результат цепочки.

```java
Flux<Integer> numbers = Flux.range(1, 5);

StepVerifier.create(numbers)
        .expectNext(1)
        .expectNext(2)
        .expectNext(3, 4, 5)  // несколько элементов за раз
        .verifyComplete();

// Проверить с преобразованием
Flux<String> words = Flux.just("apple", "banana", "cherry");

StepVerifier.create(words.map(String::toUpperCase))
        .expectNext("APPLE", "BANANA", "CHERRY")
        .verifyComplete();
```

## Q3. Как проверить, что Mono завершился ошибкой?

Ошибка в реактивном потоке — это сигнал `onError`, а не выброшенное исключение. Поэтому проверять её надо не через `try/catch`, а через семейство `expectError*` / `verifyError*`. У них четыре уровня строгости — выбирай по тому, что важно проверить:

- **Только тип** — `verifyError(RuntimeException.class)` (короткая форма `expectError(...).verify()`).
- **Тип и сообщение** — `expectErrorMessage("...")`.
- **Произвольный предикат** — `expectErrorMatches(ex -> ...)`, когда нужна частичная проверка.
- **Полноценные assertions** — `expectErrorSatisfies(ex -> ...)`, когда хочется использовать AssertJ/JUnit и проверить сразу несколько свойств.

```java
Mono<String> failing = Mono.error(new RuntimeException("something went wrong"));

// Проверить тип ошибки
StepVerifier.create(failing)
        .verifyError(RuntimeException.class);

// Проверить тип и сообщение
StepVerifier.create(failing)
        .expectErrorMessage("something went wrong")
        .verify();

// Проверить через предикат
StepVerifier.create(failing)
        .expectErrorMatches(ex ->
                ex instanceof RuntimeException &&
                ex.getMessage().contains("wrong"))
        .verify();

// Проверить через consumer
StepVerifier.create(failing)
        .expectErrorSatisfies(ex -> {
            assertThat(ex).isInstanceOf(RuntimeException.class);
            assertThat(ex.getMessage()).contains("wrong");
        })
        .verify();
```

## Q4. Как проверить, что поток пустой (empty)?

Пустой поток — это `onComplete` без единого `onNext`. Чтобы его проверить, описывают сценарий без `expectNext` и сразу вызывают `verifyComplete()`. Для `Flux` иногда явно пишут `expectNextCount(0)` — так намерение читается яснее. Тот же приём проверяет, что сработал `switchIfEmpty`: после фоллбэка поток уже не пустой и должен выдать подменное значение.

```java
// Mono.empty()
Mono<String> empty = Mono.empty();

StepVerifier.create(empty)
        .verifyComplete(); // нет expectNext — только завершение

// Flux.empty()
Flux<Integer> emptyFlux = Flux.empty();

StepVerifier.create(emptyFlux)
        .expectNextCount(0)
        .verifyComplete();

// Проверить switchIfEmpty
Mono<String> withFallback = Mono.<String>empty()
        .switchIfEmpty(Mono.just("fallback"));

StepVerifier.create(withFallback)
        .expectNext("fallback")
        .verifyComplete();
```

## Q5. Что делает verifyComplete() vs verify()?

Оба метода запускают поток, но `verifyComplete()` дополнительно встраивает ожидание успешного завершения:

- `verifyComplete()` — это сокращение `expectComplete().verify()`. Запускает поток **и** требует, чтобы он завершился сигналом `onComplete`. Если вместо этого пришла ошибка (или поток не завершился) — `AssertionError`.
- `verify()` — просто запускает поток и выполняет уже описанный сценарий, не добавляя ничего своего. Применяют, когда финальный сигнал уже задан явно — через `.expectComplete()` или `.expectError()`.

**Эмпирическое правило:** для успешного завершения пиши `verifyComplete()`, для ошибки — `expectError(...).verify()` или `verifyError(...)`.

```java
// verifyComplete() = expectComplete() + verify()
StepVerifier.create(flux)
        .expectNext(1, 2, 3)
        .verifyComplete();

// Эквивалентно:
StepVerifier.create(flux)
        .expectNext(1, 2, 3)
        .expectComplete()
        .verify();

// verify() используется явно с expectError
StepVerifier.create(failingMono)
        .expectError(IllegalStateException.class)
        .verify();
```

## Q6. Как проверить элементы с помощью expectNextMatches?

`expectNextMatches(Predicate<T>)` проверяет один элемент через предикат: вернул `true` — шаг прошёл, `false` — `AssertionError`. Это спасает в двух случаях: когда у объекта нет `equals()` (или он сравнивает не те поля), и когда нужно проверить не всё значение целиком, а лишь часть его свойств. На каждый `onNext` — отдельный `expectNextMatches`.

```java
Mono<UserDto> user = userService.findById(1L);

StepVerifier.create(user)
        .expectNextMatches(u ->
                u.id() == 1L &&
                u.name() != null &&
                !u.name().isBlank())
        .verifyComplete();

// Для Flux
Flux<OrderDto> orders = orderService.findAll();

StepVerifier.create(orders)
        .expectNextMatches(order -> order.status() == OrderStatus.PENDING)
        .expectNextMatches(order -> order.status() == OrderStatus.PROCESSING)
        .verifyComplete();
```

## Q7. Как проверить N элементов через expectNextCount?

`expectNextCount(n)` проверяет только **количество** элементов, не сверяя их значения. Это нужно, когда элементов много и важен лишь их объём, или когда конкретные значения непредсказуемы. Метод можно комбинировать: сначала пропустить пачку через `expectNextCount`, затем точечно проверить следующие элементы через `expectNext`. Чтобы досрочно оборвать бесконечный (или просто длинный) поток, ставят `thenCancel()` вместо ожидания завершения.

```java
Flux<Integer> bigFlux = Flux.range(1, 1000);

StepVerifier.create(bigFlux)
        .expectNextCount(1000)
        .verifyComplete();

// Пропустить первые K, проверить следующие N
StepVerifier.create(bigFlux)
        .expectNextCount(500)              // пропустить 500
        .expectNext(501, 502)              // проверить следующие 2
        .thenCancel()                      // отменить подписку
        .verify();
```

## Q8. Как использовать assertNext для сложных проверок?

`assertNext(Consumer<T>)` отдаёт элемент в consumer, внутри которого можно использовать полноценные AssertJ/JUnit assertions. В отличие от `expectNextMatches` (возвращает `boolean` — при провале видишь лишь «predicate failed»), `assertNext` при ошибке даёт **внятное сообщение** от assertion-библиотеки: какое поле не совпало и какие были значения. Поэтому для сложных объектов с несколькими проверками предпочтительнее `assertNext`.

```java
Mono<UserProfileDto> profile = profileService.getProfile(1L);

StepVerifier.create(profile)
        .assertNext(p -> {
            assertThat(p.user().id()).isEqualTo(1L);
            assertThat(p.orders()).isNotEmpty();
            assertThat(p.orders()).allMatch(o ->
                    o.userId().equals(1L));
            assertThat(p.subscription()).isNotNull();
        })
        .verifyComplete();
```

## Q9. Как тестировать сигналы с помощью recordWith?

`recordWith(Supplier<Collection>)` вместе с `expectRecordedMatches` собирает **все** прошедшие элементы в коллекцию и проверяет их разом — как единое целое. Это удобно, когда порядок элементов не гарантирован или когда проверка относится ко всему набору (размер, агрегат, отсутствие дубликатов), а не к каждому элементу по отдельности. Типичная связка: `recordWith(...)` → `thenConsumeWhile(...)` (потребить элементы) → `expectRecordedMatches(...)`.

Для проверки одного элемента подряд достаточно `consumeNextWith` — он работает как `assertNext`, но без агрегации.

```java
Flux<Integer> numbers = Flux.range(1, 10)
        .filter(n -> n % 2 == 0);

List<Integer> collected = new ArrayList<>();

StepVerifier.create(numbers)
        .recordWith(() -> collected)
        .thenConsumeWhile(n -> true)           // потребить все элементы
        .expectRecordedMatches(list ->
                list.size() == 5 &&
                list.stream().allMatch(n -> n % 2 == 0))
        .verifyComplete();

// Или просто через consumeNextWith для одного элемента
StepVerifier.create(Flux.just("a", "b"))
        .consumeNextWith(s -> assertThat(s).isEqualTo("a"))
        .consumeNextWith(s -> assertThat(s).isEqualTo("b"))
        .verifyComplete();
```

## Q10. Как тестировать Hot publishers через StepVerifier?

Hot publisher излучает элементы независимо от подписчиков — события «текут» сами по себе, и опоздавший подписчик пропустит то, что прошло до него. Из-за этого нельзя просто `create()` и ждать: к моменту подписки эмиссия может уже закончиться.

**Приём:** управлять эмиссией изнутри сценария через `.then(() -> ...)`. `StepVerifier` сначала подписывается, и только потом, шаг за шагом, мы сами вызываем `emitNext` в нужные моменты — так гонка между подпиской и эмиссией исключается. Источником служит `Sinks` (или `ConnectableFlux`).

```java
// Тестирование через Sinks
Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();

Flux<String> hotFlux = sink.asFlux();

StepVerifier.create(hotFlux)
        .then(() -> sink.emitNext("event1", Sinks.EmitFailureHandler.FAIL_FAST))
        .expectNext("event1")
        .then(() -> sink.emitNext("event2", Sinks.EmitFailureHandler.FAIL_FAST))
        .expectNext("event2")
        .then(() -> sink.emitComplete(Sinks.EmitFailureHandler.FAIL_FAST))
        .verifyComplete();
```

## Q11. Что такое виртуальное время (Virtual Time) и зачем оно нужно?

**Проблема:** операторы с задержками (`delay`, `interval`, `timeout`) планируют события на реальном времени. Тест, проверяющий таймаут в 30 секунд, честно прождёт 30 секунд — а тестов могут быть сотни.

**Решение:** `VirtualTimeScheduler` подменяет планировщик: время становится управляемой шкалой, которую двигает сам тест. Метод `thenAwait(duration)` «проматывает» виртуальные часы мгновенно, заставляя все запланированные на этот интервал события сработать сразу. Реальное время при этом почти не тратится, а логика поведения проверяется честно.

```java
// БЕЗ виртуального времени — тест выполняется 5 секунд
StepVerifier.create(Mono.delay(Duration.ofSeconds(5)))
        .expectNext(0L)
        .verifyComplete();

// С виртуальным временем — мгновенно
StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofSeconds(5)))
        .thenAwait(Duration.ofSeconds(5))  // "промотать" время
        .expectNext(0L)
        .verifyComplete();
```

## Q12. Как использовать StepVerifier.withVirtualTime?

`withVirtualTime` принимает **`Supplier<Publisher>`** (лямбду), а не готовый publisher — и это не каприз API. Сначала `StepVerifier` подменяет планировщик на `VirtualTimeScheduler`, и только потом вызывает лямбду — поэтому операторы внутри неё привязываются к виртуальному времени.

Если же создать publisher заранее (вне лямбды), его таймеры уже захватят настоящий планировщик до подмены — и `thenAwait` на них не подействует, тест зависнет на реальной задержке. Это самая частая ошибка при работе с виртуальным временем.

```java
// ПРАВИЛЬНО — Supplier создаёт publisher внутри
StepVerifier.withVirtualTime(() ->
        Flux.interval(Duration.ofSeconds(1)).take(3))
        .thenAwait(Duration.ofSeconds(3))
        .expectNext(0L, 1L, 2L)
        .verifyComplete();

// НЕПРАВИЛЬНО — publisher создан до withVirtualTime
Flux<Long> alreadyCreated = Flux.interval(Duration.ofSeconds(1)).take(3); // НЕ ТАК
StepVerifier.withVirtualTime(() -> alreadyCreated); // виртуальное время не работает
```

## Q13. Как тестировать delayElements и interval?

Оба оператора растягивают эмиссию во времени, поэтому тестируются под `withVirtualTime` с чередованием `thenAwait` и `expectNext`. Логика одинаковая: промотать время → проверить, что пришло.

- `delayElements(d)` — задерживает **каждый** элемент на `d`. После первого `thenAwait` приходит первый элемент, после второго — второй, и так далее.
- `interval(d)` — выдаёт счётчик `0, 1, 2, ...` каждые `d`. Можно промотать сразу весь интервал и проверить всю пачку.

```java
// delayElements — задержка каждого элемента
StepVerifier.withVirtualTime(() ->
        Flux.just("a", "b", "c")
            .delayElements(Duration.ofSeconds(1)))
        .thenAwait(Duration.ofSeconds(1))
        .expectNext("a")
        .thenAwait(Duration.ofSeconds(1))
        .expectNext("b")
        .thenAwait(Duration.ofSeconds(1))
        .expectNext("c")
        .verifyComplete();

// interval — периодические события
StepVerifier.withVirtualTime(() ->
        Flux.interval(Duration.ofMinutes(1)).take(5))
        .thenAwait(Duration.ofMinutes(5))
        .expectNext(0L, 1L, 2L, 3L, 4L)
        .verifyComplete();
```

## Q14. Как тестировать timeout с виртуальным временем?

Оператор `timeout(d)` бросает `TimeoutException`, если источник молчит дольше `d`. Виртуальное время позволяет проверить оба сценария мгновенно:

- **Таймаут сработал** — источник, который никогда не отвечает (`Mono.never()`), плюс промотка времени за порог таймаута. Ожидаем `TimeoutException`.
- **Уложились в срок** — источник отдаёт значение раньше порога; промотка до момента эмиссии даёт нормальный результат без ошибки.

```java
// Тест: операция должна завершиться с TimeoutException
StepVerifier.withVirtualTime(() ->
        Mono.<String>never()           // никогда не завершается
            .timeout(Duration.ofSeconds(10)))
        .thenAwait(Duration.ofSeconds(10))  // промотать время до таймаута
        .verifyError(TimeoutException.class);

// Тест: операция завершается РАНЬШЕ таймаута
StepVerifier.withVirtualTime(() ->
        Mono.just("ok")
            .delayElement(Duration.ofSeconds(3))
            .timeout(Duration.ofSeconds(10)))
        .thenAwait(Duration.ofSeconds(3))
        .expectNext("ok")
        .verifyComplete();
```

## Q15. Что такое TestPublisher и когда его использовать?

`TestPublisher<T>` — это «ручной» источник, которым управляет тест: вы сами решаете, что и когда эмитировать. Если `StepVerifier` — это про проверку получателя сигналов, то `TestPublisher` — про точное управление их отправителем.

**Сценарии применения:**
- Подать асинхронное событие именно в тот момент, когда нужно по сценарию.
- Проверить реакцию потребителя на конкретную, в том числе нештатную, последовательность сигналов.
- Тестировать backpressure и нарушения спецификации Reactive Streams (см. Q17–Q18).

Часто `TestPublisher` и `StepVerifier` работают в паре: первый эмитирует сигналы внутри `.then(...)`, второй их проверяет.

```java
TestPublisher<String> publisher = TestPublisher.create();

StepVerifier.create(publisher.flux().map(String::toUpperCase))
        .then(() -> publisher.emit("hello", "world"))
        .expectNext("HELLO", "WORLD")
        .then(publisher::complete)
        .verifyComplete();
```

## Q16. Как эмитировать элементы и ошибки через TestPublisher?

У `TestPublisher` есть несколько методов эмиссии, и важно понимать, какой сигнал каждый из них шлёт:

- `next(...)` — один или несколько `onNext`, поток остаётся открытым.
- `complete()` — `onComplete`, успешное завершение.
- `error(ex)` — `onError`, завершение с ошибкой.
- `emit(...)` — сокращение: `next(...)` плюс `complete()` одним вызовом.

В сценарии `StepVerifier` эти вызовы оборачивают в `.then(() -> ...)`, чтобы они срабатывали в нужный момент — уже после подписки.

```java
TestPublisher<Integer> publisher = TestPublisher.create();

// Эмитировать элементы
publisher.next(1);
publisher.next(2, 3, 4);

// Завершить успешно
publisher.complete();

// Завершить с ошибкой
publisher.error(new RuntimeException("test error"));

// emit = next + complete
publisher.emit(1, 2, 3);

// В StepVerifier
StepVerifier.create(publisher.flux())
        .then(() -> publisher.next(10))
        .expectNext(10)
        .then(() -> publisher.error(new IllegalStateException("oops")))
        .verifyError(IllegalStateException.class);
```

## Q17. Как тестировать backpressure через TestPublisher?

Здесь `TestPublisher` решает две задачи. Первая — играть роль источника, у которого тест точно контролирует запросы: `StepVerifier.create(flux, n)` задаёт начальный `request(n)`, а `thenRequest(...)` досыпает спрос — так проверяется, что цепочка отдаёт ровно столько, сколько запрошено. Вторая — проверить **сам контракт спроса** методами `assertMinRequested` / `assertMaxRequested`: убедиться, что подписчик действительно запросил столько, сколько ожидалось.

Если же нужно смоделировать **нарушителя** backpressure (источник эмитит больше запрошенного), берут `TestPublisher.Behavior.REQUEST_OVERFLOW` через `createNoncompliant` (см. Q18) — так проверяют, что защитный оператор корректно реагирует на overflow.

```java
// Проверить что оператор корректно обрабатывает backpressure
TestPublisher<Integer> publisher = TestPublisher.create();

StepVerifier.create(publisher.flux(), 1) // запросить только 1 элемент
        .then(() -> publisher.next(1))
        .expectNext(1)
        .thenRequest(2)                  // затем ещё 2
        .then(() -> publisher.next(2, 3))
        .expectNext(2, 3)
        .thenCancel()
        .verify();

// Проверить что подписчик сделал правильное число запросов
publisher.assertMinRequested(1);
publisher.assertMaxRequested(Long.MAX_VALUE);
```

## Q18. Как создать TestPublisher без соблюдения спецификации Reactive Streams?

По умолчанию `TestPublisher` сам соблюдает спецификацию `Reactive Streams` и бросает исключение, если попытаться её нарушить (эмитировать `null`, превысить спрос, продолжить после `complete`). Но иногда именно нарушитель и нужен — чтобы проверить, как поведёт себя ваш оператор, столкнувшись с некорректным источником.

`TestPublisher.createNoncompliant(Behavior...)` отключает соответствующие проверки. Доступные нарушения:
- `ALLOW_NULL` — разрешить эмиссию `null`.
- `REQUEST_OVERFLOW` — эмитировать больше, чем запрошено.
- `DEFER_CANCELLATION` — продолжить эмиссию после сигнала завершения.

```java
// Нарушение: эмитировать null (обычно запрещено)
TestPublisher<String> noncompliant = TestPublisher.createNoncompliant(
        TestPublisher.Behavior.ALLOW_NULL);

// Нарушение: эмитировать больше чем запрошено (overflow)
TestPublisher<Integer> overflowing = TestPublisher.createNoncompliant(
        TestPublisher.Behavior.REQUEST_OVERFLOW);

// Нарушение: эмитировать после complete
TestPublisher<String> postComplete = TestPublisher.createNoncompliant(
        TestPublisher.Behavior.DEFER_CANCELLATION);
```

## Q19. Что такое WebTestClient?

`WebTestClient` — реактивный тестовый HTTP-клиент для `Spring WebFlux`, аналог `MockMvc` из мира MVC. Он позволяет дёрнуть эндпоинт и проверить статус, заголовки и тело ответа через текучий (fluent) API, не блокируя поток.

Главное его удобство — выбор уровня изоляции под нужную скорость и достоверность теста:
- **Без сервера, только контроллер** — `bindToController(...)`, `bindToRouterFunction(...)`. Быстро, для unit-тестов веб-слоя.
- **Реальный сервер** — `bindToServer(...)`. Медленнее, но проверяет настоящий HTTP-стек.
- **Полный контекст Spring Boot** — `@AutoConfigureWebTestClient`. Клиент уже сконфигурирован и готов к `@Autowired`.

## Q20. Как настроить WebTestClient для тестирования WebFlux-контроллера?

Самый частый подход — срез `@WebFluxTest(Controller.class)`: Spring поднимает **только** веб-слой (один контроллер), а зависимости подставляют `@MockBean`. Это быстро и изолированно. `WebTestClient` при этом уже сконфигурирован — достаточно `@Autowired`.

Альтернатива для функциональных эндпоинтов (`RouterFunction`) — собрать клиент вручную через `bindToRouterFunction(...)`, без контекста Spring вообще.

Сценарий теста единообразный: настроить мок (`given(...)`) → `webTestClient.get()...exchange()` → проверить ответ.

```java
// Вариант 1: без сервера — только контроллер
@ExtendWith(SpringExtension.class)
@WebFluxTest(UserController.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    void findById_shouldReturnUser() {
        UserDto user = new UserDto(1L, "Alice");
        given(userService.findById(1L)).willReturn(Mono.just(user));

        webTestClient.get()
                .uri("/api/v1/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserDto.class)
                .isEqualTo(user);
    }
}

// Вариант 2: функциональный роутер
@Test
void routerFunction_test() {
    WebTestClient client = WebTestClient
            .bindToRouterFunction(userRoutes(userHandler))
            .build();

    client.get().uri("/api/v1/users")
            .exchange()
            .expectStatus().isOk();
}
```

## Q21. Как проверить тело ответа через WebTestClient?

После `exchange()` тело проверяют одним из трёх способов — по тому, что важнее проверить:

- **Типизированный объект** — `expectBody(Type.class)`, дальше `isEqualTo(...)` (сравнить целиком) или `value(obj -> ...)` (проверить отдельные поля через AssertJ).
- **Список** — `expectBodyList(Type.class)` с проверками `hasSize`, `contains` и т.п.
- **Сырой JSON** — `expectBody().jsonPath("$.field")...`, когда десериализовать в DTO не нужно или проверяется структура ошибки.

Статус ответа проверяют до тела: `expectStatus().isOk()`, `isNotFound()` и т.д.

```java
// Проверить один объект
webTestClient.get()
        .uri("/api/v1/users/1")
        .exchange()
        .expectStatus().isOk()
        .expectBody(UserDto.class)
        .value(user -> {
            assertThat(user.id()).isEqualTo(1L);
            assertThat(user.name()).isEqualTo("Alice");
        });

// Проверить список
webTestClient.get()
        .uri("/api/v1/users")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(UserDto.class)
        .hasSize(3)
        .contains(new UserDto(1L, "Alice"));

// Проверить JSON напрямую
webTestClient.get()
        .uri("/api/v1/users/1")
        .exchange()
        .expectBody()
        .jsonPath("$.id").isEqualTo(1)
        .jsonPath("$.name").isEqualTo("Alice");

// Проверить статус ошибки
webTestClient.get()
        .uri("/api/v1/users/999")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody()
        .jsonPath("$.message").isEqualTo("User not found: 999");
```

## Q22. Как тестировать SSE через WebTestClient?

SSE-эндпоинт (`text/event-stream`) возвращает **бесконечный** или долгоживущий поток, поэтому обычное `expectBody()` не подойдёт — оно ждало бы завершения, которого нет. Решение — получить тело как реактивный поток и подключить к нему `StepVerifier`:

1. Запросить с `accept(MediaType.TEXT_EVENT_STREAM)`.
2. После `exchange()` взять `returnResult(Type.class).getResponseBody()` — это `Flux` событий.
3. Ограничить поток через `take(n)` и передать в `StepVerifier::create`.
4. Проверить нужные события и обязательно завершить `thenCancel()` — иначе подписка повиснет на бесконечном потоке.

```java
@Test
void streamEvents_shouldReturnSse() {
    webTestClient.get()
            .uri("/api/v1/events")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus().isOk()
            .returnResult(String.class)
            .getResponseBody()
            .take(3)                     // взять первые 3 события
            .as(StepVerifier::create)
            .expectNextCount(3)
            .thenCancel()
            .verify();
}

// Тест SSE с проверкой содержимого
@Test
void priceStream_shouldReceivePrices() {
    webTestClient.get()
            .uri("/api/v1/prices")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .returnResult(PriceDto.class)
            .getResponseBody()
            .take(5)
            .as(StepVerifier::create)
            .expectNextMatches(p -> p.amount().compareTo(BigDecimal.ZERO) > 0)
            .expectNextCount(4)
            .thenCancel()
            .verify();
}
```

## Q23. Как проверить, что publisher соблюдает backpressure?

Идея проверки backpressure — **управлять спросом вручную** и убеждаться, что publisher отдаёт ровно столько, сколько запрошено, не больше.

`StepVerifier.create(flux, initialRequest)` задаёт начальный `request(n)` (вместо безлимитного по умолчанию). Дальше `thenRequest(n)` досыпает спрос порциями, а `thenRequest(Long.MAX_VALUE)` снимает ограничение. Между запросами `expectNext` проверяет, что пришли именно те элементы. Для кастомного источника дополнительно подтверждают спрос через `TestPublisher.assertMinRequested(...)`.

```java
Flux<Integer> flux = Flux.range(1, 10);

StepVerifier.create(flux, 3)  // запросить только 3 элемента
        .expectNext(1, 2, 3)
        .thenRequest(2)        // запросить ещё 2
        .expectNext(4, 5)
        .thenRequest(Long.MAX_VALUE) // запросить всё остальное
        .expectNext(6, 7, 8, 9, 10)
        .verifyComplete();

// Проверить, что кастомный publisher не отправляет лишнего
TestPublisher<Integer> controlled = TestPublisher.create();
StepVerifier.create(controlled.flux(), 1)
        .then(() -> {
            controlled.assertMinRequested(1);
            controlled.next(1);
        })
        .expectNext(1)
        .thenCancel()
        .verify();
```

## Q24. Как тестировать onBackpressureDrop через StepVerifier?

`onBackpressureDrop(consumer)` отбрасывает элементы, которые не помещаются в текущий спрос, и передаёт каждый отброшенный в callback. Отсюда стратегия теста: ограничить спрос, проверить, что получены **только** запрошенные элементы, а **всё остальное** попало в drop-callback.

1. Завести список `dropped` и передать его как callback `onBackpressureDrop(dropped::add)`.
2. Запросить ограниченное число элементов (`create(flux, 5)`), проверить их через `expectNext`, оборвать `thenCancel()`.
3. После `verify()` — убедиться, что в `dropped` лежат именно отброшенные элементы.

```java
@Test
void backpressureDrop_shouldDropExcessElements() {
    List<Integer> dropped = new ArrayList<>();

    Flux<Integer> flux = Flux.range(1, 100)
            .onBackpressureDrop(dropped::add);

    StepVerifier.create(flux, 5)  // запросить только 5
            .expectNext(1, 2, 3, 4, 5)
            .thenCancel()
            .verify();

    // Элементы 6-100 должны быть сброшены
    assertThat(dropped).containsExactlyElementsOf(
            IntStream.rangeClosed(6, 100).boxed().toList());
}
```

## Q25. Почему нельзя вызывать block() в тестах Spring WebFlux?

Потому что `block()` блокирует поток до завершения publisher, а в `WebFlux` всё крутится на нескольких потоках event loop (`Netty`, `reactor-http-nio-*`). Заблокировать такой поток — значит застопорить обработку всех остальных запросов. Reactor это распознаёт и превентивно бросает `IllegalStateException: block()/blockFirst()/blockLast() are blocking, which is not supported in thread reactor-xxx`.

**Правильно:** не извлекать значение через `block()`, а проверять поток как поток — через `StepVerifier`. Если `block()` всё же неизбежен (например, в интеграционном тесте вне event loop), его выносят на `Schedulers.boundedElastic()` или запускают тест под `@SpringBootTest`, где главный поток не принадлежит event loop.

```java
// НЕПРАВИЛЬНО — block() в тесте вызовет исключение
@Test
void bad() {
    UserDto user = userController.getById(1L).block(); // исключение!
    assertThat(user.name()).isEqualTo("Alice");
}

// ПРАВИЛЬНО — через StepVerifier
@Test
void good() {
    StepVerifier.create(userController.getById(1L))
            .assertNext(user -> assertThat(user.name()).isEqualTo("Alice"))
            .verifyComplete();
}

// Если всё же нужен block() — добавить .publishOn(Schedulers.boundedElastic())
// или использовать @SpringBootTest вне event loop
```

## Q26. Что произойдёт, если не вызвать verify() / verifyComplete()?

Тест **молча пройдёт, ничего не проверив** — и это коварнее, чем падение. Цепочка `StepVerifier` ленивая: до `verify()` / `verifyComplete()` подписки на publisher нет, а значит, нет ни прогона потока, ни сравнения сигналов. Все `expectNext` остаются лишь описанием, которое никто не выполнил.

Опасность в том, что зелёный тест создаёт ложную уверенность: код может быть сломан, а тест этого не заметит. **Подводный камень:** последним звеном цепочки `StepVerifier` всегда должен быть терминальный вызов — `verify()`, `verifyComplete()`, `verifyError(...)` или `verifyErrorMessage(...)`.

```java
// НЕПРАВИЛЬНО — verify() не вызван, тест ничего не проверяет
@Test
void bad() {
    StepVerifier.create(mono)
            .expectNext("expected"); // создали верификатор, но не запустили!
}

// ПРАВИЛЬНО — обязательно завершить цепочку вызовом verify
@Test
void good() {
    StepVerifier.create(mono)
            .expectNext("expected")
            .verifyComplete(); // обязательно!
}

// Также: verifyError(Class), verifyErrorMessage(String)
```

## Q27. Как тестировать реактивные методы с @Transactional?

В реактивном стеке транзакция привязана к контексту подписки (Reactor Context), а не к потоку, поэтому нужен **реактивный** `TransactionManager` (`R2dbcTransactionManager`), а не классический потоко-локальный. Срез `@DataR2dbcTest` поднимает только слой данных и подставляет его автоматически, а `@Transactional` на тестовом классе откатывает изменения после каждого теста — БД остаётся чистой.

**Подводный камень:** обычный `@Transactional` из JDBC-мира с блокирующим менеджером в реактивном коде не сработает — транзакция просто не «подхватится» цепочкой. Сам тест по-прежнему строится на `StepVerifier`.

```java
@DataR2dbcTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void save_shouldPersistUser() {
        User user = new User(null, "testuser", "test@example.com");

        StepVerifier.create(
                userRepository.save(user)
                        .flatMap(saved -> userRepository.findById(saved.id()))
        )
                .assertNext(found -> {
                    assertThat(found.username()).isEqualTo("testuser");
                    assertThat(found.id()).isNotNull();
                })
                .verifyComplete();
    }
}
```

## Q28. Как правильно замокать реактивные зависимости через Mockito?

Реактивный метод возвращает не значение, а `Mono`/`Flux`, поэтому в `given(...).willReturn(...)` подставляют именно publisher, а не «голый» объект. Главное — выбрать правильный пустой/наполненный тип под сценарий:

- найдено — `Mono.just(value)`;
- не найдено — `Mono.empty()` (а не `null`! `null` сломает цепочку с NPE);
- ошибка — `Mono.error(ex)`;
- коллекция — `Flux.just(...)`.

Результат всё равно проверяют через `StepVerifier`, а не `block()`. Дополнительных библиотек не нужно: Mockito работает с `Mono`/`Flux` как с обычными возвращаемыми объектами.

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findById_shouldReturnUser() {
        User user = new User(1L, "Alice", "alice@example.com");
        given(userRepository.findById(1L)).willReturn(Mono.just(user));

        StepVerifier.create(userService.findById(1L))
                .assertNext(dto -> assertThat(dto.name()).isEqualTo("Alice"))
                .verifyComplete();
    }

    @Test
    void findById_whenNotFound_shouldReturnEmpty() {
        given(userRepository.findById(999L)).willReturn(Mono.empty());

        StepVerifier.create(userService.findById(999L))
                .verifyComplete(); // ожидаем пустой Mono
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        given(userRepository.findAll()).willReturn(
                Flux.just(
                        new User(1L, "Alice", "alice@example.com"),
                        new User(2L, "Bob", "bob@example.com")
                ));

        StepVerifier.create(userService.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }
}
```

## See also

- [Project Reactor](project-reactor-interview.md)
- [Reactive Streams](reactive-streams-interview.md)
- [RxJava](rxjava-interview.md)
- [Spring WebFlux](../frameworks/spring/spring-webflux-interview.md)
- [Spring Framework](../frameworks/spring/spring-framework-interview.md)
- [Reactive Patterns](reactive-patterns-interview.md)
- [[testing-interview|Тестирование]]
