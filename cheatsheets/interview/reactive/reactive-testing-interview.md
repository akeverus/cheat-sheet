---
title: "Вопросы на собеседовании: Тестирование реактивного кода"
description: "Тестирование реактивного кода: StepVerifier, TestPublisher, виртуальное время, WebTestClient, тестирование backpressure"
tags:
  - interview
  - reactive
  - reactive-testing-interview
  - testing
  - stepverifier
aliases:
  - "Reactive Testing interview"
  - "StepVerifier interview"
  - "TestPublisher interview"
  - "реактивное тестирование собеседование"
  - "WebTestClient interview"
difficulty: "intermediate"
updated: "2026-04-20"
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

`StepVerifier` — тестовый компонент из `reactor-test`, позволяющий декларативно верифицировать события в реактивной цепочке. Создаёт подписчика, записывает сигналы (`onNext`, `onError`, `onComplete`) и сравнивает их с ожидаемым сценарием.

**Принцип работы:**
1. `StepVerifier.create(publisher)` — создаёт верификатор и подписывается
2. Цепочка `expectNext*` / `expectError*` / `expectComplete` — описывает ожидаемые сигналы
3. `.verify()` или `.verifyComplete()` — запускает реактивный поток и блокирует тест до завершения

```java
// Зависимость: io.projectreactor:reactor-test (test scope)

Mono<String> greeting = Mono.just("Hello, Reactor!");

StepVerifier.create(greeting)
        .expectNext("Hello, Reactor!")
        .verifyComplete();
```

## Q2. Как проверить элементы Flux через StepVerifier?

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

- `verifyComplete()` — ожидает `onComplete` и бросает `AssertionError` если получена ошибка или поток не завершился
- `verify()` — только запускает поток без проверки типа завершения. Используется когда ожидание уже описано через `.expectComplete()` или `.expectError()`

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

`expectNextMatches(Predicate<T>)` — проверяет один элемент через предикат. Удобно для частичных проверок.

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

`assertNext(Consumer<T>)` — проверяет элемент через consumer с поддержкой AssertJ/JUnit assertions.

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

`recordWith(Supplier<Collection>)` + `expectRecordedMatches` позволяет собрать все элементы в коллекцию для проверки сразу.

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

Hot publishers начинают излучать элементы независимо от подписчиков. Для тестирования используют `Sinks` или `ConnectableFlux`.

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

**Проблема:** тесты с реальными задержками (`delay`, `interval`, `timeout`) выполняются столько же, сколько и в продакшене — минуты/часы.

**Решение:** `VirtualTimeScheduler` заменяет системные часы управляемыми. Реальное время не тратится — тест управляет «виртуальным» временем через `thenAwait`.

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

Важно передать `Supplier<Publisher>` (лямбда), а не готовый publisher — иначе таймер уже запустится до подмены планировщика.

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

`TestPublisher<T>` — управляемый publisher для тестирования: можно вручную эмитировать элементы, ошибки и сигналы завершения. Полезен когда нужно:
- Симулировать асинхронные события в нужный момент
- Тестировать, как потребитель реагирует на конкретную последовательность сигналов
- Тестировать backpressure

```java
TestPublisher<String> publisher = TestPublisher.create();

StepVerifier.create(publisher.flux().map(String::toUpperCase))
        .then(() -> publisher.emit("hello", "world"))
        .expectNext("HELLO", "WORLD")
        .then(publisher::complete)
        .verifyComplete();
```

## Q16. Как эмитировать элементы и ошибки через TestPublisher?

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

`TestPublisher.Behavior.REQUEST_OVERFLOW` — позволяет TestPublisher нарушать backpressure (эмитировать больше, чем запрошено), чтобы проверить реакцию потребителя.

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

По умолчанию `TestPublisher` соблюдает правила `Reactive Streams` и бросает исключение при нарушении. `TestPublisher.createNoncompliant()` отключает эти проверки.

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

`WebTestClient` — реактивный тестовый HTTP-клиент для `Spring WebFlux`. Поддерживает:
- Тестирование без запуска сервера (через `bindToController`, `bindToRouterFunction`)
- Тестирование реального сервера (`bindToServer`)
- Интеграцию со `Spring Boot Test` (`@AutoConfigureWebTestClient`)

## Q20. Как настроить WebTestClient для тестирования WebFlux-контроллера?

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

Через `StepVerifier.create(flux, initialRequest)` — второй параметр задаёт начальный запрос. Затем `thenRequest(n)` добавляет дополнительные запросы.

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

В контексте `WebFlux` / `Netty` event loop `block()` вызовет `IllegalStateException: block()/blockFirst()/blockLast() are blocking, which is not supported in thread reactor-xxx`.

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

Тест не выполнит подписку. Реактивная цепочка — ленивая, без вызова `verify()` Publisher не будет подписан и никакой проверки не произойдёт. Тест пройдёт даже при некорректном поведении.

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

`R2DBC` + `@Transactional` работает реактивно. В тестах используют `@DataR2dbcTest` или `@Transactional` на тестовом классе с реактивным `TransactionManager`.

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

Mockito работает с `Mono`/`Flux` — нужно возвращать реактивные типы из `given()`.

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
