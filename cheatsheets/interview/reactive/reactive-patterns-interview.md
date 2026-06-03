---
title: "Вопросы на собеседовании: Reactive Patterns"
description: "Реактивные паттерны: circuit breaker, retry, timeout, bulkhead, scatter-gather, reactive saga, backpressure стратегии"
tags:
  - interview
  - reactive
  - reactive-patterns-interview
  - resilience
  - patterns
type: "interview"
difficulty: "advanced"
aliases:
  - "Вопросы на собеседовании"
  - "Reactive Patterns"
  - "Reactive Patterns interview"
  - "Reactive Patterns вопросы"
prerequisites: []
next: []
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
- [Q21. Что делает onBackpressureDrop и когда его использовать?](#q21-что-делает-onbackpressuredrop-и-когда-его-использовать)

**Паттерны компоновки**
- [Q22. Что такое паттерн Cache-Aside в реактивном контексте?](#q22-что-такое-паттерн-cache-aside-в-реактивном-контексте)
- [Q23. Как реализовать паттерн Fallback Chain?](#q23-как-реализовать-паттерн-fallback-chain)
- [Q24. Как реализовать паттерн Hedging (дублирующие запросы)?](#q24-как-реализовать-паттерн-hedging-дублирующие-запросы)
- [Q25. Что такое паттерн Request Coalescing?](#q25-что-такое-паттерн-request-coalescing)
- [Q26. Как реализовать паттерн Debounce/Throttle в Reactor?](#q26-как-реализовать-паттерн-debouncethrottle-в-reactor)

---

## Q1. Что такое Circuit Breaker и зачем он нужен в реактивной системе?

`Circuit Breaker` — паттерн устойчивости, который временно прекращает вызовы к нестабильному сервису, чтобы не нагружать упавшую зависимость и не тратить ресурсы на заведомо обречённые запросы. Как только сервис восстанавливается, вызовы автоматически возобновляются.

**Аналогия:** электрический предохранитель. При коротком замыкании он размыкает цепь, защищая остальную проводку от перегорания. Circuit Breaker делает то же с вызовами сервисов.

**Почему критичен именно в реактивной системе.** В блокирующем мире зависший вызов держит один поток. В реактивном — цепочки операторов делят небольшой пул event-loop'ов: если зависшие вызовы накапливаются, они забивают worker-пул, и встаёт уже вся система, а не один сервис. Circuit Breaker рвёт эту цепочку накопления отказов.

**Что он даёт:**
- **Быстрый отказ** (`fail fast`) — вместо того чтобы каждый запрос ждал полного таймаута, открытый breaker сразу возвращает ошибку. Потоки не простаивают.
- **Защита от каскадных отказов** — падение одного сервиса не «протекает» вверх по цепочке зависимостей и не роняет соседей.
- **Автоматическое восстановление** — breaker периодически пропускает пробные вызовы и сам решает, когда зависимость снова здорова. Ручного вмешательства не требуется.

## Q2. Какие состояния есть у Circuit Breaker?

У Circuit Breaker три состояния, между которыми он переключается как конечный автомат в зависимости от доли ошибок:

```
CLOSED ──(превышен порог ошибок)──> OPEN ──(прошло время ожидания)──> HALF_OPEN
   ^                                                                         |
   └──────────────(пробный вызов успешен)────────────────────────────────────┘
                                         (пробный вызов провалился)──> OPEN
```

- **CLOSED** (нормальная работа) — все запросы проходят к сервису, а breaker считает долю ошибок в скользящем окне. Как только она превышает порог — переход в OPEN.
- **OPEN** (цепь разомкнута) — запросы к сервису не идут вообще, breaker мгновенно отклоняет их с `CallNotPermittedException`. Это и есть быстрый отказ. По истечении времени ожидания — переход в HALF_OPEN.
- **HALF_OPEN** (проверка) — breaker пропускает ограниченное число пробных запросов. Если они успешны — сервис ожил, возврат в CLOSED. Если хоть один пробный провалился — снова OPEN, и отсчёт ожидания начинается заново.

Ключевая идея: OPEN защищает упавший сервис от добивания нагрузкой, а HALF_OPEN аккуратно прощупывает восстановление, не открывая шлюз сразу всему трафику.

## Q3. Как интегрировать Resilience4j Circuit Breaker с Project Reactor?

Модуль `resilience4j-reactor` даёт оператор `CircuitBreakerOperator`, который оборачивает любой `Mono`/`Flux`. Подключают его через `transformDeferred(...)` — отложенный вариант, чтобы breaker применялся на каждую подписку заново, а не один раз при сборке цепочки.

Конфигурация breaker'а из примера читается так: открыться при 50% ошибок (`failureRateThreshold`) в окне из 10 вызовов (`slidingWindowSize`), держать OPEN 30 секунд (`waitDurationInOpenState`), затем в HALF_OPEN пропустить 3 пробных вызова (`permittedNumberOfCallsInHalfOpenState`).

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

Fallback вешают через `onErrorResume`, реагируя на конкретный тип ошибки. Когда breaker открыт, он бросает `CallNotPermittedException` — её и перехватывают, подменяя ответ заглушкой или данными из кэша. Так пользователь получает хоть какой-то результат вместо ошибки.

Полезно различать причины отказа: на `CallNotPermittedException` (breaker открыт) отдаём кэш, а на `WebClientResponseException` (сервис ответил ошибкой) — дефолтное «неизвестно». Порядок `onErrorResume` важен: каждый ловит свой тип, более специфичные ставят раньше.

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

private Mono<UserDto> getCachedUser(Long id) {
    return redisTemplate.opsForValue()
            .get("user:" + id)
            .cast(UserDto.class);
}
```

## Q5. Как реализовать retry с экспоненциальным backoff?

Через `retryWhen(Retry.backoff(...))` — встроенную в Reactor (3.3+) спецификацию повторов. Экспоненциальный backoff означает, что задержка между попытками растёт по экспоненте: 500мс, 1с, 2с... — так не добивают восстанавливающийся сервис частыми повторами.

Обязательный спутник backoff — **jitter** (случайный разброс задержки). Без него все клиенты, упавшие в одну секунду, повторят запрос синхронно и создадут «thundering herd» — волну одновременной нагрузки. Jitter размазывает повторы во времени.

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

**Параметры:**
- `3` — максимум повторов после первой неудачи (то есть всего до 4 попыток)
- `Duration.ofMillis(500)` — начальная задержка перед первым повтором, дальше удваивается
- `maxBackoff` — потолок задержки, чтобы экспонента не разрасталась до минут
- `jitter(0.5)` — разброс ±50% от задержки, чтобы повторы разных клиентов не совпадали

## Q6. Чем retryWhen отличается от retry?

Коротко: `retry(n)` — это «повторить тупо N раз сразу», а `retryWhen(RetrySpec)` — это «повторить по правилам, которые ты задал».

- `retry(n)` — повторяет N раз немедленно, без паузы, на **любую** ошибку. В продакшене опасен: при недоступном сервисе он мгновенно выстрелит N запросов подряд и только усилит нагрузку на падающую зависимость.
- `retryWhen(RetrySpec)` — принимает спецификацию, где можно задать задержку и backoff, фильтр по типу ошибки (повторять только transient-сбои), jitter и callback'и. Это рабочий вариант для прода.

```java
// retry — простое повторение (не рекомендуется для продакшена)
flux.retry(3);

// retryWhen — полный контроль
flux.retryWhen(
    Retry.fixedDelay(3, Duration.ofSeconds(2))
         .filter(ex -> ex instanceof TransientException)
         .onRetryExhaustedThrow((spec, signal) ->
             new MaxRetriesExceededException(signal.failure()))
);
```

## Q7. Как ограничить retry по типу исключения?

Через `.filter(predicate)` на `RetrySpec` — повтор срабатывает, только если предикат вернул `true`.

Это важная гигиена retry: повторять имеет смысл лишь **transient-ошибки**, которые могут исчезнуть сами (таймауты, временная недоступность, 503). Повторять детерминированные ошибки бессмысленно и вредно — `ValidationException` или 4xx (неверный запрос) при повторе провалятся точно так же, только зря потратят попытки и нагрузят сервис.

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

Оператор `.timeout(Duration)`. Если за указанное время на `Mono`/`Flux` не пришёл сигнал (для `Mono` — значение, для `Flux` — следующий элемент), оператор отменяет источник и бросает `TimeoutException`. Это страховка от зависших вызовов, которые иначе держали бы цепочку бесконечно.

Дальше с таймаутом обычно делают одно из двух:
- **Переименовать ошибку** через `onErrorMap` — обернуть низкоуровневый `TimeoutException` в доменное исключение с понятным сообщением.
- **Подставить fallback** — у `timeout` есть перегрузка с запасным `Mono`, который отдаётся вместо ошибки при срабатывании дедлайна.

```java
Mono<UserDto> userWithTimeout = userService.findById(id)
        .timeout(Duration.ofSeconds(3))
        .onErrorMap(TimeoutException.class,
                ex -> new ServiceTimeoutException("User service timeout after 3s"));

// timeout с fallback
Mono<UserDto> withFallback = userService.findById(id)
        .timeout(Duration.ofSeconds(3),
                 Mono.just(UserDto.unknown(id)));  // fallback при таймауте
```

## Q9. Чем timeout() отличается от delaySubscription()?

Это операторы про разные концы жизненного цикла подписки, и путать их легко по названию.

- `timeout(Duration)` — **дедлайн**. Подписка уже идёт; если результат не пришёл за время `T`, операция отменяется с ошибкой. Контролирует, как долго мы ждём ответа.
- `delaySubscription(Duration)` — **ленивый старт**. Откладывает сам момент подписки на время `T`: запрос не уйдёт раньше, чем пройдёт `T`. Контролирует, когда мы начинаем.

То есть один ограничивает ожидание сверху, другой сдвигает старт вперёд. В паттерне Hedging (Q24) `delaySubscription` как раз используют, чтобы запасной запрос стартовал чуть позже основного.

```java
// timeout — установить дедлайн
Mono<String> response = externalService.call()
        .timeout(Duration.ofSeconds(5)); // отменить если не ответил за 5 сек

// delaySubscription — ленивый старт (например, после прогрева)
Mono<String> delayed = externalService.call()
        .delaySubscription(Duration.ofSeconds(2)); // подождать 2 сек перед первым запросом
```

## Q10. Что такое паттерн Bulkhead?

`Bulkhead` (буквально «переборка») — ограничение числа одновременных вызовов к ресурсу, чтобы один деградирующий сервис не съел все ресурсы системы и не утянул за собой остальных.

**Аналогия — откуда название.** В корпусе корабля делают водонепроницаемые переборки: пробоина затапливает один отсек, но не весь трюм. Так же и здесь: «отсек» каждого сервиса ограничен квотой вызовов, и его захлёбывание не топит всю систему.

**Два вида реализации:**
- **Semaphore-based** — счётчик-семафор ограничивает число конкурентных вызовов. Превышение — мгновенный отказ, без очереди.
- **Thread pool-based** — каждому сервису выдаётся отдельный пул потоков, и его исчерпание изолирует проблему внутри этого пула.

**Применимость в реактивном мире.** Thread pool bulkhead тут менее уместен: реактивный код по природе не привязывает вызов к выделенному потоку. Поэтому используют `Semaphore` bulkhead (см. Q11) или ограничивают конкурентность прямо в `flatMap` (см. Q12).

## Q11. Как реализовать Bulkhead через Resilience4j?

Через `BulkheadOperator`, который вешают на цепочку тем же `transformDeferred`, что и Circuit Breaker. Семафорный bulkhead держит счётчик активных вызовов: `maxConcurrentCalls(10)` пропускает максимум 10 параллельно, а `maxWaitDuration(Duration.ZERO)` означает «не ждать освобождения слота — сразу отказать».

При переполнении breaker бросает `BulkheadFullException`. Её перехватывают через `onErrorResume` и превращают в осмысленную «сервис перегружен», вместо того чтобы пускать запрос дальше и усугублять давку.

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

Вторым аргументом `flatMap(mapper, concurrency)`. По умолчанию `flatMap` подписывается на все inner-потоки сразу (concurrency = 256), и при большом входном потоке это лавиной создаёт сотни параллельных вызовов. Параметр `concurrency` ставит потолок: одновременно активны не больше N inner-подписок, остальные ждут освобождения слота.

Это самый лёгкий встроенный bulkhead — без библиотек. Несколько полезных вариантов:
- `flatMap(mapper, N)` — порядок результатов не гарантируется (быстрее).
- `flatMapSequential(mapper, N)` — та же конкурентность, но результаты отдаются в исходном порядке.
- `buffer(M).flatMap(..., N)` — обрабатывать батчами по `M` элементов, не больше `N` батчей разом.

```java
// Обработать список ID, максимум 5 параллельных вызовов
Flux<UserDto> users = Flux.fromIterable(ids)
        .flatMap(id -> userService.findById(id), 5); // concurrency = 5

// Аналогично с flatMapSequential (сохраняет порядок)
Flux<UserDto> orderedUsers = Flux.fromIterable(ids)
        .flatMapSequential(id -> userService.findById(id), 5);

// Обработка батчей
Flux<Void> processed = Flux.fromIterable(events)
        .buffer(100)   // батчи по 100
        .flatMap(batch -> processor.processBatch(batch), 3); // 3 батча параллельно
```

## Q13. Что такое паттерн Scatter-Gather?

`Scatter-Gather` — разослать запрос нескольким получателям параллельно (`scatter`) и собрать их ответы в один результат (`gather`). Суть в том, что вызовы идут одновременно, поэтому общее время ≈ время самого медленного источника, а не сумма всех.

**Сценарии применения:**
- **Сборка составного ответа** — одновременно дёрнуть несколько микросервисов и склеить из них один DTO (профиль = пользователь + заказы + отзывы).
- **Fan-out в поиске** — разослать запрос по разным индексам/шардам и слить результаты.
- **Сравнение источников** — собрать котировки от нескольких провайдеров и выбрать лучшую.

В Reactor для фиксированного набора источников это `Mono.zip` (Q14), а граничный вопрос — что делать, если один источник упал (Q15).

## Q14. Как реализовать Scatter-Gather через Mono.zip?

`Mono.zip(a, b, c)` подписывается на все источники одновременно и ждёт, пока каждый отдаст значение, после чего собирает их в `Tuple`. Дальше `.map` превращает кортеж в нужный DTO. Источники выполняются параллельно — это и есть scatter, а `zip` — gather.

Два варианта под разные задачи:
- **Фиксированный набор источников** — перечислить их в `Mono.zip(...)` и разобрать `Tuple` по `getT1()/getT2()/...`.
- **Переменное число источников** — собрать `List<Mono<...>>` и передать в `Mono.zip(list, combinator)`. Здесь удобен `Mono.zipDelayError`: он не падает на первой же ошибке, а дожидается остальных источников и пробрасывает ошибку только в конце.

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

    return Mono.zipDelayError(priceSources, prices ->
            Arrays.stream(prices)
                  .map(p -> (PriceDto) p)
                  .min(Comparator.comparing(PriceDto::amount))
                  .orElseThrow());
}
```

## Q15. Как обработать частичные отказы в Scatter-Gather?

Проблема: `Mono.zip` устроен по принципу «всё или ничего» — стоит одному источнику упасть, как весь zip завершается ошибкой, даже если остальные ответили. Часто это неоправданно: профиль можно показать и без блока заказов.

Решение — навесить `.onErrorResume` на **каждый необязательный источник по отдельности**, ещё до того как он попадёт в `zip`. Тогда сбой такого источника превращается в безопасное значение по умолчанию (пустой список, `Optional.empty()`), и `zip` спокойно собирает итог. Обязательные источники (например, сам пользователь) fallback'ом не оборачивают — если упал он, провалиться должен весь запрос.

**Эмпирическое правило:** разделите источники на критичные и опциональные. Критичные пусть роняют zip, опциональным дайте дефолт.

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

    return Mono.zip(user, orders, subscription)
            .map(tuple -> UserProfileDto.builder()
                    .user(tuple.getT1())
                    .orders(tuple.getT2())
                    .subscription(tuple.getT3().orElse(null))
                    .build());
}
```

## Q16. Что такое Saga Pattern и при чём тут реактивность?

`Saga` — способ управлять распределённой транзакцией там, где обычный ACID-транзакции через несколько сервисов нет. Длинную бизнес-операцию разбивают на цепочку локальных транзакций, и к каждому шагу заранее готовят **компенсирующее действие** — операцию отката. Если шаг N упал, сага в обратном порядке компенсирует уже выполненные шаги, возвращая систему в согласованное состояние.

Важно: компенсация — это не rollback БД, а семантический «анти-шаг» (не «отменить списание», а «вернуть деньги»). Поэтому сага даёт не атомарность, а **итоговую согласованность**.

**При чём тут реактивность.** Сага — это по сути последовательность асинхронных шагов с обработкой ошибок, и реактивная цепочка ложится на неё один в один: каждый шаг — `flatMap`, а компенсация — `onErrorResume`, который перед пробросом ошибки запускает откат. Получается читаемый линейный код без callback-ада.

```
createOrder → reserveStock → processPayment → sendNotification
    ↓                ↓               ↓
cancelOrder    releaseStock   refundPayment   (компенсация)
```

## Q17. Как реализовать хореографическую сагу через события?

У саги два стиля координации, и важно их различать:

- **Хореография** — единого дирижёра нет. Каждый сервис подписан на события и в ответ публикует свои: `OrderCreated` → сервис склада ловит его, резервирует и публикует `StockReserved` → сервис оплаты ловит это и т.д. Связность низкая, но общий ход саги «размазан» по сервисам, и его сложно отследить.
- **Оркестрация** — есть центральный оркестратор, который явно вызывает шаги по порядку и сам разруливает компенсацию. Поток саги виден в одном месте.

В реактивном Reactor оркестрация выражается особенно наглядно — цепочкой `flatMap`, где в каждый шаг встроен `onErrorResume` с откатом предыдущих. Пример ниже показывает именно оркестрационный вариант: видно, как при сбое шага 2 откатывается шаг 1, а при сбое шага 3 — шаги 2 и 1.

```java
// Оркестратор саги (оркестрационный вариант)
@Service
public class OrderSagaOrchestrator {

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

Компенсация — это операция отмены уже выполненного шага, которую запускают при сбое последующего. Удобно завернуть эту логику в хелпер `runWithCompensation(action, compensation)`: если `action` упал, сначала выполняется `compensation`, и только потом исходная ошибка пробрасывается дальше.

Два нетривиальных момента, которые видно в коде ниже:
- **Сбой самой компенсации нельзя «потерять» молча, но и нельзя дать ему перебить исходную ошибку.** Поэтому ошибку отката логируют и гасят (`Mono.empty()`), а наверх всё равно уходит первоначальная причина (`then(Mono.error(ex))`). Иначе диагностировать, что реально сломалось, станет невозможно.
- **Компенсация выполняется только при ошибке action.** Если шаг прошёл успешно, откатывать нечего.

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

Backpressure — это ситуация, когда producer выдаёт элементы быстрее, чем consumer успевает обрабатывать. Reactor не даёт одному решению на все случаи, а предлагает выбрать стратегию исходя из того, что важнее: ничего не потерять или не переполнить память.

| Стратегия | Оператор | Поведение | Когда выбирать |
|---|---|---|---|
| Буферизация | `onBackpressureBuffer()` | Копить лишнее в очереди | Терять данные нельзя, всплески коротки |
| Сброс | `onBackpressureDrop()` | Отбрасывать лишние элементы | Потеря отдельных значений терпима (метрики) |
| Последний | `onBackpressureLatest()` | Хранить только последний элемент | Важно только актуальное (котировки, датчики) |
| Ошибка | `onBackpressureError()` | Бросить `OverflowException` | Перегрузка — это явный баг, лучше упасть |

Главный компромисс — между буферизацией (рискуем памятью и OOM) и сбросом (теряем данные). Универсального ответа нет: выбор диктует семантика данных.

## Q20. Что делает onBackpressureBuffer?

Складывает в очередь элементы, которые producer выдал быстрее, чем consumer их забирает, — consumer разгребает их в своём темпе. Это спасает от потери данных при коротких всплесках нагрузки.

Главная опасность — **неограниченный буфер**: если producer стабильно быстрее consumer, очередь растёт без предела и приводит к `OutOfMemoryError`. Поэтому в проде задают границу и поведение при её достижении:
- `onBackpressureBuffer(maxSize)` — ограничить размер очереди.
- callback на сброшенный элемент — для учёта потерь.
- `BufferOverflowStrategy` — что делать при полном буфере: `DROP_OLDEST` (выкинуть самый старый), `DROP_LATEST` или `ERROR`.

```java
Flux.interval(Duration.ofMillis(1))    // очень быстрый producer
        .onBackpressureBuffer(1000,         // буфер на 1000 элементов
                dropped -> log.warn("Dropped: {}", dropped),  // callback при переполнении
                BufferOverflowStrategy.DROP_OLDEST)           // стратегия при полном буфере
        .flatMap(i -> processSlowly(i), 1) // медленный consumer
        .subscribe();
```

## Q21. Что делает onBackpressureDrop и когда его использовать?

`onBackpressureDrop()` просто выбрасывает элементы, которые consumer не успевает принять, — без очереди и без памяти под буфер. Это противоположность `onBackpressureBuffer`: там данные берегут ценой памяти, здесь экономят память ценой данных.

Подходит, когда **актуальность важнее полноты** и потеря отдельных значений не ломает логику:
- **Метрики и мониторинг** — пропуск одного замера среди тысяч некритичен.
- **Live-стриминг** — буферить устаревшие кадры бессмысленно, нужны свежие.

Опциональный callback на сброшенный элемент полезен, чтобы хотя бы считать потери в метрике. Близкий родственник — `onBackpressureLatest()`: он не отбрасывает всё подряд, а всегда хранит последнее значение (идеально для котировок и датчиков, где важно только текущее состояние).

```java
Flux.interval(Duration.ofMillis(10))
        .onBackpressureDrop(dropped ->
                metrics.incrementCounter("events.dropped")) // учёт потерь
        .flatMap(i -> heavyProcessing(i), 2)
        .subscribe();

// onBackpressureLatest — хранить только последнее значение (для котировок, датчиков)
sensorFlux
        .onBackpressureLatest()
        .flatMap(reading -> store(reading), 1)
        .subscribe();
```

## Q22. Что такое паттерн Cache-Aside в реактивном контексте?

`Cache-Aside` (он же `Lazy Loading`) — кэшем управляет само приложение, а не библиотека прозрачно за кулисами. Логика всегда одна и та же из трёх шагов: проверить кэш → при промахе загрузить из источника → положить загруженное в кэш на будущее. Данные попадают в кэш лениво, по первому запросу.

В реактивном коде эти три шага складываются в естественную цепочку: `get(key)` отдаёт `Mono`, пустой результат подхватывает `switchIfEmpty` (это и есть промах кэша), внутри которого идёт загрузка из БД и запись обратно через `flatMap(... .thenReturn(value))`.

**Подводный камень:** при cache-miss под высокой нагрузкой сотни запросов одновременно промахнутся и одновременно ударят в БД (cache stampede). Смягчают это паттерном Request Coalescing (Q25).

```java
@Service
public class CachedUserService {

    private final ReactiveRedisTemplate<String, UserDto> redis;
    private final UserRepository userRepository;

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

`Fallback Chain` — выстроить источники по убыванию приоритета и при ошибке очередного автоматически переключаться на следующий, пока какой-нибудь не ответит. Так система деградирует плавно, а не падает на первом же сбое.

Реализуется цепочкой `onErrorResume`, где каждое звено ловит конкретный тип ошибки и подставляет следующий источник: основной сервис → резервный → локальный кэш → заглушка. Заглушка в конце гарантирует, что ответ будет всегда, пусть и неполный.

**Нюанс:** фильтруйте по типу ошибки. Если ловить любую (`onErrorResume(ex -> ...)`), под фоллбэк попадут и баги вроде `NullPointerException`, которые надо чинить, а не маскировать. Перехватывайте только ожидаемые сбои источника (`ServiceUnavailableException`, `CacheMissException`).

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

`Hedging` — отправить один и тот же запрос нескольким серверам и взять первый пришедший успешный ответ. Идея в том, что медленный отдельный узел не задержит весь запрос: если основной завис, ответит другой.

Главная польза — **снижение хвостовых задержек** (p99, p999). Средняя латентность от hedging почти не меняется, а вот редкие «застрявшие» запросы перестают тянуть хвост распределения вверх.

В Reactor это `Mono.firstWithValue(...)` — он подписывается на все источники и эмитит первый, который отдал значение (ошибки игнорируются, пока есть надежда на других). Чтобы не удваивать нагрузку зря, запасной запрос обычно стартуют с задержкой через `delaySubscription` — он уйдёт, только если основной не уложился в небольшой бюджет времени.

**Компромисс:** hedging увеличивает суммарный объём запросов к бэкенду, поэтому применять его стоит к идемпотентным чтениям и с разумной задержкой запасного запроса.

```java
public Mono<UserDto> findUserWithHedging(Long id) {
    Mono<UserDto> primary = primaryService.findById(id);
    Mono<UserDto> secondary = secondaryService.findById(id)
            .delaySubscription(Duration.ofMillis(50)); // запустить через 50ms если primary медленный

    return Mono.firstWithValue(primary, secondary); // взять первый успешный
}

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

`Request Coalescing` (схлопывание запросов) — если несколько одинаковых запросов прилетели одновременно, выполнить работу один раз, а результат раздать всем ожидающим. Классический пример: 100 пользователей в одну секунду запросили один и тот же ресурс — в источник уходит ровно один запрос вместо ста.

Это прямое лекарство от **cache stampede** (Q22): без coalescing все промахи кэша синхронно ударяют в БД, с ним — бьёт только первый, остальные ждут его результат.

**Как работает в коде:** держим `ConcurrentHashMap` «летящих» запросов. `computeIfAbsent(id, ...)` гарантирует, что для ключа создаётся единственный `Mono`; `.cache()` запоминает его результат и отдаёт всем подписчикам, а `doFinally` убирает запись из карты после завершения, чтобы следующий запрос пошёл заново.

```java
// Реализация через Mono.cache() + ConcurrentHashMap
@Service
public class CoalescingService {

    private final ConcurrentHashMap<Long, Mono<UserDto>> inFlight = new ConcurrentHashMap<>();

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

Все три оператора прореживают слишком частый поток событий, но по разной логике — и выбор зависит от того, какой именно элемент периода вам нужен:

- `debounce(Duration)` — ждёт **тишины**: эмитит элемент, только если за ним `Duration` ничего не пришло. Идеален для поиска по вводу — запрос уходит, когда пользователь перестал печатать, а не на каждое нажатие.
- `throttleFirst(Duration)` — пропускает **первый** элемент за период и глушит остальные. Подходит для защиты от дабл-кликов и частых кнопок.
- `sample(Duration)` — берёт **последний** элемент за период. Хорош для метрик и котировок, где нужен свежий снимок раз в N секунд, а не каждое изменение.

Коротко: debounce — «дождаться паузы», throttleFirst — «первый в окне», sample — «последний в окне».

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

- [Project Reactor](project-reactor-interview.md)
- [Reactive Streams](reactive-streams-interview.md)
- [RxJava](rxjava-interview.md)
- [Spring WebFlux](../frameworks/spring/spring-webflux-interview.md)
- [Spring Framework](../frameworks/spring/spring-framework-interview.md)
- [Тестирование реактивного кода](reactive-testing-interview.md)
- [Распределённые системы](../architecture/distributed-systems-interview.md)
