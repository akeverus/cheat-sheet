---
title: "Вопросы на собеседовании: Reactive Patterns"
description: "Реактивные паттерны: circuit breaker, retry, timeout, bulkhead, scatter-gather, reactive saga, backpressure стратегии"
tags:
  - interview
  - reactive
  - reactive-patterns-interview
  - resilience
  - patterns
aliases:
  - "Reactive Patterns interview"
  - "Reactive Patterns собеседование"
  - "Reactive Patterns вопросы"
  - "circuit breaker reactive"
  - "resilience4j reactor"
difficulty: "advanced"
updated: "2026-04-25"
---
# Вопросы на собеседовании: Reactive Patterns

Гид по паттернам устойчивости и архитектурным паттернам в реактивных системах для `Senior Java Developer`. Охватывает `Circuit Breaker`, `Retry`, `Timeout`, `Bulkhead`, `Scatter-Gather`, `Reactive Saga`, а также стратегии backpressure и паттерны composability реактивных потоков.

**Реактивные паттерны** — набор архитектурных решений для построения устойчивых (resilient), масштабируемых и отзывчивых систем на основе реактивных потоков. Многие паттерны из классической отказоустойчивости (Circuit Breaker, Retry) имеют реактивную реализацию через операторы `Project Reactor` и `Resilience4j`.

## Полезные ссылки

### Официальная документация

- [Resilience4j + Reactor](https://resilience4j.readme.io/docs/getting-started-3) — интеграция с Project Reactor
- [Reactive Manifesto](https://www.reactivemanifesto.org/) — принципы реактивных систем
- [Project Reactor Operators](https://projectreactor.io/docs/core/release/reference/#which-operator) — справочник операторов

### Статьи

- [Resilience4j CircuitBreaker](https://www.baeldung.com/resilience4j) — Circuit Breaker на Baeldung
- [Bulkhead Pattern](https://www.baeldung.com/resilience4j-bulkhead) — паттерн Bulkhead
- [Scatter-Gather Pattern](https://www.enterpriseintegrationpatterns.com/patterns/messaging/BroadcastAggregate.html) — Scatter-Gather EIP

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Circuit Breaker**
- [Q1. Что такое Circuit Breaker и зачем он нужен в реактивной системе?](#q1-что-такое-circuit-breaker-и-зачем-он-нужен-в-реактивной-системе)
- [Q2. Какие состояния есть у Circuit Breaker?](#q2-какие-состояния-есть-у-circuit-breaker)
- [Q3. Как интегрировать Resilience4j Circuit Breaker с Project Reactor?](#q3-как-интегрировать-resilience4j-circuit-breaker-с-project-reactor)
- [Q4. Как настроить fallback при открытом Circuit Breaker?](#q4-как-настроить-fallback-при-открытом-circuit-breaker)

**Retry**
- [Q5. Как реализовать retry с экспоненциальным backoff?](#q5-как-реализовать-retry-с-экспоненциальным-backoff)
- [Q6. Чем retryWhen отличается от retry?](#q6-чем-retrywhen-отличается-от-retry)
- [Q7. Как ограничить retry по типу исключения?](#q7-как-ограничить-retry-по-типу-исключения)

**Timeout**
- [Q8. Как задать таймаут на реактивную операцию?](#q8-как-задать-таймаут-на-реактивную-операцию)
- [Q9. Чем timeout() отличается от delaySubscription()?](#q9-чем-timeout-отличается-от-delaysubscription)

**Bulkhead**
- [Q10. Что такое паттерн Bulkhead?](#q10-что-такое-паттерн-bulkhead)
- [Q11. Как реализовать Bulkhead через Resilience4j?](#q11-как-реализовать-bulkhead-через-resilience4j)
- [Q12. Как ограничить конкурентность через flatMap concurrency?](#q12-как-ограничить-конкурентность-через-flatmap-concurrency)

**Scatter-Gather**
- [Q13. Что такое паттерн Scatter-Gather?](#q13-что-такое-паттерн-scatter-gather)
- [Q14. Как реализовать Scatter-Gather через Mono.zip?](#q14-как-реализовать-scatter-gather-через-monozip)
- [Q15. Как обработать частичные отказы в Scatter-Gather?](#q15-как-обработать-частичные-отказы-в-scatter-gather)

**Reactive Saga**
- [Q16. Что такое Saga Pattern и при чём тут реактивность?](#q16-что-такое-saga-pattern-и-при-чём-тут-реактивность)
- [Q17. Как реализовать хореографическую сагу через события?](#q17-как-реализовать-хореографическую-сагу-через-события)
- [Q18. Как реализовать компенсирующую транзакцию в реактивной саге?](#q18-как-реализовать-компенсирующую-транзакцию-в-реактивной-саге)

**Backpressure стратегии**
- [Q19. Какие стратегии backpressure есть в Project Reactor?](#q19-какие-стратегии-backpressure-есть-в-project-reactor)
- [Q20. Что делает onBackpressureBuffer?](#q20-что-делает-onbackpressurebuffer)
- [Q21. Что делает onBackpressueDrop и когда его использовать?](#q21-что-делает-onbackpressuredrop-и-когда-его-использовать)

**Паттерны компоновки**
- [Q22. Что такое паттерн Cache-Aside в реактивном контексте?](#q22-что-такое-паттерн-cache-aside-в-реактивном-контексте)
- [Q23. Как реализовать паттерн Fallback Chain?](#q23-как-реализовать-паттерн-fallback-chain)
- [Q24. Как реализовать паттерн Hedging (дублирующие запросы)?](#q24-как-реализовать-паттерн-hedging-дублирующие-запросы)
- [Q25. Что такое паттерн Request Coalescing?](#q25-что-такое-паттерн-request-coalescing)
- [Q26. Как реализовать паттерн Debounce/Throttle в Reactor?](#q26-как-реализовать-паттерн-debouncethrottle-в-reactor)

---

## Q1. Что такое Circuit Breaker и зачем он нужен в реактивной системе?

`Circuit Breaker` — паттерн устойчивости: прерывает вызовы к нестабильному сервису, пока тот не восстановится. В реактивной системе особенно важен — без него ошибки могут накапливаться в реактивных цепочках и блокировать worker-пулы.

**Аналогия:** электрический предохранитель — при коротком замыкании размыкает цепь, чтобы защитить остальное.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Три принципа применения:**
- Быстрый отказ (`fail fast`) вместо долгого ожидания таймаута
- Защита от каскадных отказов между сервисами
- Автоматическое восстановление через периодические пробные вызовы

## Q2. Какие состояния есть у Circuit Breaker?

```
CLOSED ──(превышен порог ошибок)──> OPEN ──(прошло время ожидания)──> HALF_OPEN
   ^                                                                         |
   └──────────────(пробный вызов успешен)────────────────────────────────────┘
                                         (пробный вызов провалился)──> OPEN
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
- **CLOSED** — все запросы проходят, ошибки считаются
- **OPEN** — все запросы немедленно отклоняются (`CallNotPermittedException`)
- **HALF_OPEN** — пропускается ограниченное число пробных запросов для проверки

## Q3. Как интегрировать Resilience4j Circuit Breaker с Project Reactor?

`resilience4j-reactor` предоставляет операторы `CircuitBreakerOperator` для оборачивания `Mono`/`Flux`.

```java
// Зависимость: io.github.resilience4j:resilience4j-reactor

@Service
public class UserService {

    private final CircuitBreaker circuitBreaker;
    private final WebClient webClient;

    public UserService(CircuitBreakerRegistry registry, WebClient webClient) {
        this.circuitBreaker = registry.circuitBreaker("userService",
                CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)           // открыть при 50% ошибок
                        .waitDurationInOpenState(Duration.ofSeconds(30))
                        .slidingWindowSize(10)
                        .permittedNumberOfCallsInHalfOpenState(3)
                        .build());
        this.webClient = webClient;
    }


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
    public Mono<UserDto> findById(Long id) {
        return webClient.get()
                .uri("/users/{id}", id)
                .retrieve()
                .bodyToMono(UserDto.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
```

## Q4. Как настроить fallback при открытом Circuit Breaker?

```java
public Mono<UserDto> findById(Long id) {
    return webClient.get()
            .uri("/users/{id}", id)
            .retrieve()
            .bodyToMono(UserDto.class)
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
            .onErrorResume(CallNotPermittedException.class,
                    ex -> getCachedUser(id))    // fallback — кэш
            .onErrorResume(WebClientResponseException.class,
                    ex -> Mono.just(UserDto.unknown(id)));
}


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
private Mono<UserDto> getCachedUser(Long id) {
    return redisTemplate.opsForValue()
            .get("user:" + id)
            .cast(UserDto.class);
}
```

## Q5. Как реализовать retry с экспоненциальным backoff?

Используется `Retry.backoff()` из `reactor-extra` или встроенный метод в Reactor 3.3+.

```java
Mono<OrderDto> orderWithRetry = orderService.createOrder(request)
        .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                .maxBackoff(Duration.ofSeconds(5))
                .jitter(0.5)  // +/-50% jitter для предотвращения thundering herd
                .doBeforeRetry(signal ->
                        log.warn("Retry attempt {}: {}",
                                signal.totalRetries(),
                                signal.failure().getMessage())));
```


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
**Параметры:**
- `3` — максимум попыток после первой неудачи
- `Duration.ofMillis(500)` — начальная задержка
- `maxBackoff` — верхняя граница задержки
- `jitter` — случайный разброс (снижает нагрузку на сервер при массовых повторах)

## Q6. Чем retryWhen отличается от retry?

- `retry(n)` — повторяет N раз немедленно, без паузы, на любую ошибку
- `retryWhen(RetrySpec)` — гибкий: можно задать задержку, фильтр по ошибке, jitter, callback

```java
// retry — простое повторение (не рекомендуется для продакшена)
flux.retry(3);


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
// retryWhen — полный контроль
flux.retryWhen(
    Retry.fixedDelay(3, Duration.ofSeconds(2))
         .filter(ex -> ex instanceof TransientException)
         .onRetryExhaustedThrow((spec, signal) ->
             new MaxRetriesExceededException(signal.failure()))
);
```

## Q7. Как ограничить retry по типу исключения?

Используется `.filter()` на `RetrySpec`.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
```java
flux.retryWhen(
    Retry.backoff(3, Duration.ofSeconds(1))
         .filter(ex ->
             ex instanceof SocketTimeoutException ||
             ex instanceof ServiceUnavailableException
             // НЕ повторять при ValidationException, 4xx и т.д.
         )
);
```

## Q8. Как задать таймаут на реактивную операцию?

Оператор `.timeout(Duration)` выбрасывает `TimeoutException` если подписчик не получает сигнал за указанное время.

```java
Mono<UserDto> userWithTimeout = userService.findById(id)
        .timeout(Duration.ofSeconds(3))
        .onErrorMap(TimeoutException.class,
                ex -> new ServiceTimeoutException("User service timeout after 3s"));


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
// timeout с fallback
Mono<UserDto> withFallback = userService.findById(id)
        .timeout(Duration.ofSeconds(3),
                 Mono.just(UserDto.unknown(id)));  // fallback при таймауте
```

## Q9. Чем timeout() отличается от delaySubscription()?

- `timeout(Duration)` — отменяет операцию если она не завершается за время `T`
- `delaySubscription(Duration)` — откладывает начало подписки на время `T`

```java
// timeout — установить дедлайн
Mono<String> response = externalService.call()
        .timeout(Duration.ofSeconds(5)); // отменить если не ответил за 5 сек


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
// delaySubscription — ленивый старт (например, после прогрева)
Mono<String> delayed = externalService.call()
        .delaySubscription(Duration.ofSeconds(2)); // подождать 2 сек перед первым запросом
```

## Q10. Что такое паттерн Bulkhead?

`Bulkhead` (`переборка`) — ограничение числа конкурентных вызовов к ресурсу. Изолирует сервисы друг от друга: деградация одного не поглощает все ресурсы системы.

**Два вида:**
- **Semaphore-based** — ограничивает число конкурентных вызовов (без очереди)
- **Thread pool-based** — каждый сервис имеет свой пул потоков


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
В реактивном мире Thread pool bulkhead менее применим — используют `Semaphore` bulkhead или ограничение конкурентности через `flatMap`.

## Q11. Как реализовать Bulkhead через Resilience4j?

```java
@Service
public class ExternalApiService {

    private final Bulkhead bulkhead;
    private final WebClient webClient;

    public ExternalApiService(BulkheadRegistry registry, WebClient webClient) {
        this.bulkhead = registry.bulkhead("externalApi",
                BulkheadConfig.custom()
                        .maxConcurrentCalls(10)       // максимум 10 параллельных вызовов
                        .maxWaitDuration(Duration.ZERO) // не ждать, сразу отказать
                        .build());
        this.webClient = webClient;
    }


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
    public Mono<ApiResponse> callApi(String request) {
        return webClient.post()
                .uri("/api/process")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .transformDeferred(BulkheadOperator.of(bulkhead))
                .onErrorResume(BulkheadFullException.class,
                        ex -> Mono.error(new ServiceOverloadedException("Too many requests")));
    }
}
```

## Q12. Как ограничить конкурентность через flatMap concurrency?

`flatMap(mapper, concurrency)` — встроенный способ ограничить число одновременно активных `inner` подписок.

```java
// Обработать список ID, максимум 5 параллельных вызовов
Flux<UserDto> users = Flux.fromIterable(ids)
        .flatMap(id -> userService.findById(id), 5); // concurrency = 5

// Аналогично с flatMapSequential (сохраняет порядок)
Flux<UserDto> orderedUsers = Flux.fromIterable(ids)
        .flatMapSequential(id -> userService.findById(id), 5);


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
// Обработка батчей
Flux<Void> processed = Flux.fromIterable(events)
        .buffer(100)   // батчи по 100
        .flatMap(batch -> processor.processBatch(batch), 3); // 3 батча параллельно
```

## Q13. Что такое паттерн Scatter-Gather?


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
`Scatter-Gather` — рассылка запроса нескольким получателям (`scatter`) и агрегация ответов (`gather`). Используется для:
- Параллельного вызова нескольких источников данных
- Fan-out в поисковых системах (запрос к разным индексам)
- Сбора котировок от нескольких провайдеров

## Q14. Как реализовать Scatter-Gather через Mono.zip?

```java
// Параллельный вызов трёх сервисов, агрегация в ProfileDto
public Mono<UserProfileDto> getUserProfile(Long userId) {
    Mono<UserDto> user = userService.findById(userId);
    Mono<List<OrderDto>> orders = orderService.findByUserId(userId)
            .collectList();
    Mono<List<ReviewDto>> reviews = reviewService.findByUserId(userId)
            .collectList();

    return Mono.zip(user, orders, reviews)
            .map(tuple -> UserProfileDto.builder()
                    .user(tuple.getT1())
                    .orders(tuple.getT2())
                    .reviews(tuple.getT3())
                    .build());
}

// Для переменного числа источников — Mono.zipDelayError
public Mono<AggregatedPriceDto> getPrices(String sku) {
    List<Mono<PriceDto>> priceSources = priceProviders.stream()
            .map(provider -> provider.getPrice(sku))
            .toList();


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
    return Mono.zipDelayError(priceSources, prices ->
            Arrays.stream(prices)
                  .map(p -> (PriceDto) p)
                  .min(Comparator.comparing(PriceDto::amount))
                  .orElseThrow());
}
```

## Q15. Как обработать частичные отказы в Scatter-Gather?

`Mono.zip` завершается ошибкой, если хотя бы один источник упал. Чтобы допустить частичный отказ — использовать `.onErrorResume` на каждом источнике.

```java
public Mono<UserProfileDto> getUserProfileWithFallbacks(Long userId) {
    Mono<UserDto> user = userService.findById(userId); // обязательно

    Mono<List<OrderDto>> orders = orderService.findByUserId(userId)
            .collectList()
            .onErrorResume(ex -> {
                log.warn("Orders service failed", ex);
                return Mono.just(List.of()); // пустой список как fallback
            });

    Mono<Optional<SubscriptionDto>> subscription = subscriptionService
            .findByUserId(userId)
            .map(Optional::of)
            .onErrorResume(ex -> Mono.just(Optional.empty()));


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
    return Mono.zip(user, orders, subscription)
            .map(tuple -> UserProfileDto.builder()
                    .user(tuple.getT1())
                    .orders(tuple.getT2())
                    .subscription(tuple.getT3().orElse(null))
                    .build());
}
```

## Q16. Что такое Saga Pattern и при чём тут реактивность?

`Saga` — паттерн управления распределёнными транзакциями: длинная транзакция разбивается на цепочку локальных транзакций с компенсирующими действиями при ошибке.

В реактивных системах сага удобно выражается как цепочка `flatMap` + обработка ошибок с компенсацией:


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
```
createOrder → reserveStock → processPayment → sendNotification
    ↓                ↓               ↓
cancelOrder    releaseStock   refundPayment   (компенсация)
```

## Q17. Как реализовать хореографическую сагу через события?

При хореографии каждый сервис реагирует на события и публикует свои события.

```java
// Оркестратор саги (оркестрационный вариант)
@Service
public class OrderSagaOrchestrator {


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
    public Mono<OrderResult> executeOrderSaga(CreateOrderRequest request) {
        return orderService.createOrder(request)               // шаг 1
                .flatMap(order ->
                    stockService.reserveStock(order)           // шаг 2
                        .onErrorResume(ex ->
                            orderService.cancelOrder(order.id()) // компенсация шага 1
                                .then(Mono.error(ex)))
                )
                .flatMap(reservation ->
                    paymentService.processPayment(reservation)  // шаг 3
                        .onErrorResume(ex ->
                            stockService.releaseStock(reservation) // компенсация шага 2
                                .then(orderService.cancelOrder(reservation.orderId()))
                                .then(Mono.error(ex)))
                )
                .flatMap(payment ->
                    notificationService.notifyUser(payment))   // шаг 4 (без компенсации)
                .map(OrderResult::success)
                .onErrorResume(ex -> Mono.just(OrderResult.failed(ex.getMessage())));
    }
}
```

## Q18. Как реализовать компенсирующую транзакцию в реактивной саге?

Компенсирующие транзакции — это операции отмены, выполняемые при ошибке в одном из шагов саги.

```java
public Mono<Void> runWithCompensation(
        Mono<Void> action,
        Mono<Void> compensation) {
    return action.onErrorResume(ex ->
            compensation
                    .onErrorResume(compEx -> {
                        log.error("Compensation failed!", compEx);
                        return Mono.empty(); // логируем, не прокидываем
                    })
                    .then(Mono.error(ex))); // всё равно пробрасываем исходную ошибку
}


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
// Использование
public Mono<OrderResult> placeOrder(CreateOrderRequest req) {
    return orderService.createOrder(req)
            .flatMap(order ->
                runWithCompensation(
                    stockService.reserve(order),
                    orderService.cancel(order.id())
                ).thenReturn(order)
            );
}
```

## Q19. Какие стратегии backpressure есть в Project Reactor?

Когда producer быстрее consumer, Reactor предлагает несколько стратегий:


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
| Стратегия | Оператор | Поведение |
|---|---|---|
| Буферизация | `onBackpressureBuffer()` | Накапливать в очереди |
| Сброс | `onBackpressureDrop()` | Отбросить лишние элементы |
| Последний | `onBackpressureLatest()` | Хранить только последний |
| Ошибка | `onBackpressureError()` | `OverflowException` при переполнении |

## Q20. Что делает onBackpressureBuffer?

Буферизует элементы, которые producer отправил быстрее, чем consumer успевает обработать.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
```java
Flux.interval(Duration.ofMillis(1))    // очень быстрый producer
        .onBackpressureBuffer(1000,         // буфер на 1000 элементов
                dropped -> log.warn("Dropped: {}", dropped),  // callback при переполнении
                BufferOverflowStrategy.DROP_OLDEST)           // стратегия при полном буфере
        .flatMap(i -> processSlowly(i), 1) // медленный consumer
        .subscribe();
```

## Q21. Что делает onBackpressureDrop и когда его использовать?

`onBackpressureDrop()` — отбрасывает элементы, которые consumer не успевает принять. Полезно для:
- Метрик и мониторинга (потеря отдельного значения не критична)
- Live-стриминга (нет смысла буферить старые кадры)

```java
Flux.interval(Duration.ofMillis(10))
        .onBackpressureDrop(dropped ->
                metrics.incrementCounter("events.dropped")) // учёт потерь
        .flatMap(i -> heavyProcessing(i), 2)
        .subscribe();


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
// onBackpressureLatest — хранить только последнее значение (для котировок, датчиков)
sensorFlux
        .onBackpressureLatest()
        .flatMap(reading -> store(reading), 1)
        .subscribe();
```

## Q22. Что такое паттерн Cache-Aside в реактивном контексте?

`Cache-Aside` (`Lazy Loading`) — приложение само управляет кэшем: сначала проверить кэш, при промахе — загрузить из источника и сохранить.

```java
@Service
public class CachedUserService {

    private final ReactiveRedisTemplate<String, UserDto> redis;
    private final UserRepository userRepository;


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
    public Mono<UserDto> findById(Long id) {
        String key = "user:" + id;
        return redis.opsForValue().get(key)      // 1. Проверить кэш
                .switchIfEmpty(
                    userRepository.findById(id)   // 2. Промах — загрузить из БД
                        .flatMap(user ->
                            redis.opsForValue()
                                .set(key, user, Duration.ofMinutes(10)) // 3. Сохранить в кэш
                                .thenReturn(user)
                        )
                );
    }
}
```

## Q23. Как реализовать паттерн Fallback Chain?

`Fallback Chain` — последовательное переключение на следующий источник при ошибке предыдущего.


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
```java
public Mono<ProductDto> findProduct(String sku) {
    return primaryCatalogService.findBySku(sku)        // 1. Основной источник
            .onErrorResume(ServiceUnavailableException.class,
                    ex -> secondaryCatalogService.findBySku(sku)) // 2. Резерв
            .onErrorResume(ServiceUnavailableException.class,
                    ex -> localCacheService.findBySku(sku))       // 3. Локальный кэш
            .onErrorResume(CacheMissException.class,
                    ex -> Mono.just(ProductDto.placeholder(sku)));  // 4. Заглушка
}
```

## Q24. Как реализовать паттерн Hedging (дублирующие запросы)?

`Hedging` — отправить запрос нескольким серверам одновременно, взять первый ответ. Снижает хвостовые задержки (p99, p999).

```java
public Mono<UserDto> findUserWithHedging(Long id) {
    Mono<UserDto> primary = primaryService.findById(id);
    Mono<UserDto> secondary = secondaryService.findById(id)
            .delaySubscription(Duration.ofMillis(50)); // запустить через 50ms если primary медленный

    return Mono.firstWithValue(primary, secondary); // взять первый успешный
}


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
// Через Flux.firstWithValue для N источников
public Mono<SearchResult> hedgedSearch(String query) {
    return Mono.firstWithValue(
            searchNode1.search(query),
            searchNode2.search(query),
            searchNode3.search(query)
    );
}
```

## Q25. Что такое паттерн Request Coalescing?

`Request Coalescing` (`схлопывание запросов`) — объединение нескольких одинаковых параллельных запросов в один. Если 100 пользователей одновременно запросили один и тот же ресурс — выполнить один запрос, раздать результат всем.

```java
// Реализация через Mono.cache() + ConcurrentHashMap
@Service
public class CoalescingService {

    private final ConcurrentHashMap<Long, Mono<UserDto>> inFlight = new ConcurrentHashMap<>();


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
    public Mono<UserDto> findById(Long id) {
        return inFlight.computeIfAbsent(id,
                key -> userRepository.findById(key)
                        .cache()                           // memoize результат
                        .doFinally(signal ->
                                inFlight.remove(key)));    // убрать после завершения
    }
}
```

## Q26. Как реализовать паттерн Debounce/Throttle в Reactor?

- `debounce(Duration)` — откладывает элемент до тех пор, пока поток не замолчит на `Duration`. Подходит для поиска по вводу.
- `throttleFirst(Duration)` — пропускает только первый элемент за период `Duration`.
- `sample(Duration)` — берёт последний элемент за период.

```java
// Debounce — поиск по вводу (не запускать пока пользователь набирает)
Flux<String> searchResults = searchInput
        .debounce(Duration.ofMillis(300))         // подождать 300ms тишины
        .distinctUntilChanged()                   // не запрашивать тот же текст
        .flatMap(query -> searchService.search(query));

// Throttle — ограничить частоту кликов
Flux<Void> buttonClicks = clickEvents
        .throttleFirst(Duration.ofSeconds(1));    // максимум 1 клик в секунду

// Sample — взять последнее значение каждые N секунд (для метрик, котировок)
Flux<BigDecimal> sampledPrices = priceStream
        .sample(Duration.ofSeconds(5));           // снять значение каждые 5 сек
```

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение 2-3 предложения
> - [ ] Вариант А | Почему неверно 2-3 предложения
> - [ ] Вариант В | Почему неверно 2-3 предложения
> - [ ] Вариант С | Почему неверно 2-3 предложения
- [Project Reactor](project-reactor-interview.md)
- [Reactive Streams](reactive-streams-interview.md)
- [RxJava](rxjava-interview.md)
- [Spring WebFlux](../frameworks/spring/spring-webflux-interview.md)
- [Spring Framework](../frameworks/spring/spring-framework-interview.md)
- [Тестирование реактивного кода](reactive-testing-interview.md)
- [Распределённые системы](../architecture/distributed-systems-interview.md)
