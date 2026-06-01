---
title: "Вопросы на собеседовании: Spring Retry"
description: "Spring Retry для автоматического повтора операций: @Retryable, @Recover, RetryTemplate, backoff-стратегии, интеграция с транзакциями и Reactor"
tags:
  - interview
  - spring
  - spring-retry-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring Retry"
  - "Spring Retry interview"
  - "Spring Retry собеседование"
prerequisites:
  - "[[spring-retry]]"
next: []
updated: "2026-05-15"
---
# Вопросы на собеседовании: `Spring Retry`

`Spring Retry` — библиотека автоматического повтора неудавшихся операций. Предоставляет аннотационный (`@Retryable`/`@Recover`) и программный (`RetryTemplate`) API. Тесно связана с `Spring Batch` и `Spring Integration`, где используется по умолчанию.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring Retry GitHub](https://github.com/spring-projects/spring-retry) — официальный репозиторий с документацией
- [Baeldung: Spring Retry](https://www.baeldung.com/spring-retry) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы и аннотации**
- [Q1. (!) Что такое Spring Retry и для каких задач он предназначен?](#q1-что-такое-spring-retry-и-для-каких-задач-он-предназначен)
- [Q2. Как подключить Spring Retry?](#q2-как-подключить-spring-retry)
- [Q3. (!) Как работает @Retryable?](#q3-как-работает-retryable)
- [Q4. (!) Что такое @Recover и каковы требования к сигнатуре?](#q4-что-такое-recover-и-каковы-требования-к-сигнатуре)
- [Q5. Какие backoff-стратегии поддерживает Spring Retry?](#q5-какие-backoff-стратегии-поддерживает-spring-retry)

**RetryTemplate и политики**
- [Q6. Что такое RetryTemplate и когда его использовать?](#q6-что-такое-retrytemplate-и-когда-его-использовать)
- [Q7. Какие RetryPolicy реализации есть в Spring Retry?](#q7-какие-retrypolicy-реализации-есть-в-spring-retry)
- [Q8. Что такое CircuitBreakerRetryPolicy?](#q8-что-такое-circuitbreakerretrypolicy)

**Ограничения и интеграции**
- [Q9. (!) Почему @Retryable не работает при self-invocation?](#q9-почему-retryable-не-работает-при-self-invocation)
- [Q10. (!) Как правильно комбинировать @Retryable и @Transactional?](#q10-как-правильно-комбинировать-retryable-и-transactional)
- [Q11. Как Spring Retry интегрируется с Reactor/WebClient?](#q11-как-spring-retry-интегрируется-с-reactorwebclient)
- [Q12. Что такое Stateful Retry и когда он нужен?](#q12-что-такое-stateful-retry-и-когда-он-нужен)

**Мониторинг и тестирование**
- [Q13. Как добавить метрики и логирование через RetryListener?](#q13-как-добавить-метрики-и-логирование-через-retrylistener)
- [Q14. Как настроить разные retry-политики для разных методов?](#q14-как-настроить-разные-retry-политики-для-разных-методов)
- [Q15. Что произойдёт, если @Recover выбросит исключение?](#q15-что-произойдёт-если-recover-выбросит-исключение)
- [Q16. Как протестировать логику retry в тестах?](#q16-как-протестировать-логику-retry-в-тестах)
- [Q17. Чем Spring Retry отличается от Resilience4j?](#q17-чем-spring-retry-отличается-от-resilience4j)

## Q1. (!) Что такое Spring Retry и для каких задач он предназначен?

`Spring Retry` — библиотека для автоматического повтора операций при сбоях. Применяется для:
- HTTP-обращений к нестабильным внешним API
- Сетевых вызовов с временными ошибками (таймауты, 5xx)
- Операций с БД при дедлоках или `OptimisticLockException`
- Чтения из очередей (Kafka, JMS)

**Основной принцип:** при получении заданного исключения метод вызывается повторно заданное число раз с настраиваемой паузой между попытками.

**Итог:** Spring Retry = простой retry. Для Circuit Breaker, Bulkhead и Rate Limiter — используй [Resilience4j](resilience4j-interview.md).

## Q2. Как подключить Spring Retry?

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<!-- AOP обязателен для аннотационного подхода -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
</dependency>
```

Аннотационный подход требует `@EnableRetry`:
```java
@Configuration
@EnableRetry
public class AppConfig {}
```

В Spring Boot `spring-retry` включается автоматически при наличии зависимости. `@EnableRetry` всё равно нужен явно.

## Q3. (!) Как работает @Retryable?

```java
@Retryable(
    retryFor = {IOException.class, TimeoutException.class},
    noRetryFor = {ValidationException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)
)
public PaymentResult processPayment(PaymentRequest req) {
    return gateway.charge(req);
}
```

**Параметры:**
- `retryFor` — исключения для retry (бывший `value`)
- `noRetryFor` — исключения без retry (бывший `exclude`)
- `maxAttempts` — максимум попыток (включая первую, default: 3)
- `backoff` — задержка между попытками

**Что происходит при исчерпании попыток:** выбрасывается последнее исключение (или вызывается `@Recover`).

## Q4. (!) Что такое @Recover и каковы требования к сигнатуре?

`@Recover` — метод-fallback, вызывается **после исчерпания всех попыток**.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    log.error("Payment failed after retries for {}", req.getId(), e);
    return PaymentResult.failed("Service unavailable");
}
```

**Требования к сигнатуре:**
- Тот же класс, что и `@Retryable`
- **Первый аргумент** — тип пойманного исключения (или суперкласс)
- **Тот же возвращаемый тип**, что и у `@Retryable`
- Остальные аргументы — те же, что у `@Retryable` метода

**Выбор @Recover:** Spring выбирает наиболее специфичный метод по типу исключения — работает как overloaded handlers.

```java
@Recover
public PaymentResult recoverIOException(IOException e, PaymentRequest req) {
    return PaymentResult.retry();          // специфичный
}

@Recover
public PaymentResult recoverGeneral(Exception e, PaymentRequest req) {
    return PaymentResult.failed("error"); // общий fallback
}
```

## Q5. Какие backoff-стратегии поддерживает Spring Retry?

```java
// Фиксированная задержка (1 сек)
@Backoff(delay = 1000)

// Экспоненциальная: 1s → 2s → 4s
@Backoff(delay = 1000, multiplier = 2.0)

// Экспоненциальная с потолком: 1s → 2s → 4s → 10s → 10s
@Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)

// С джиттером (предотвращает thundering herd)
@Backoff(delay = 1000, multiplier = 2.0, random = true)
```

**Thundering Herd:** если все клиенты синхронно ретраят после сбоя, они создают волну нагрузки. Джиттер разбрасывает попытки по времени.

**Программный BackOffPolicy:**
```java
ExponentialBackOffPolicy backOff = new ExponentialBackOffPolicy();
backOff.setInitialInterval(1000);
backOff.setMultiplier(2.0);
backOff.setMaxInterval(10000);
```

## Q6. Что такое RetryTemplate и когда его использовать?

`RetryTemplate` — программный API для retry без аннотаций.

**Используй когда:**
- Нужен retry не в Spring bean (utility-метод, лямбда)
- Retry-политика должна конфигурироваться динамически
- Нужен явный recover-callback прямо в коде
- Нужен `stateful retry` (Kafka, JMS)

```java
@Bean
public RetryTemplate retryTemplate() {
    return RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(1000, 2, 10000)
        .retryOn(IOException.class)
        .withListener(new CustomRetryListener())
        .build();
}

// Использование
retryTemplate.execute(
    ctx -> externalApi.call(data),      // операция
    ctx -> fallbackResult(data)         // recover callback
);
```

## Q7. Какие RetryPolicy реализации есть в Spring Retry?

| Политика | Описание |
|---|---|
| `SimpleRetryPolicy` | Фиксированное число попыток по типам исключений |
| `AlwaysRetryPolicy` | Повторять бесконечно (осторожно!) |
| `NeverRetryPolicy` | Никогда не повторять (для тестов) |
| `TimeoutRetryPolicy` | Повторять до истечения таймаута |
| `ExceptionClassifierRetryPolicy` | Разные политики для разных исключений |
| `CircuitBreakerRetryPolicy` | Встроенный Circuit Breaker |
| `CompositeRetryPolicy` | Комбинация политик (AND/OR логика) |

```java
// ExceptionClassifierRetryPolicy
ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
policy.setPolicyMap(Map.of(
    IOException.class, new SimpleRetryPolicy(3),
    TimeoutException.class, new SimpleRetryPolicy(5),
    IllegalArgumentException.class, new NeverRetryPolicy()
));
```

## Q8. Что такое CircuitBreakerRetryPolicy?

`CircuitBreakerRetryPolicy` — встроенный Circuit Breaker в Spring Retry (в отличие от полноценного [Resilience4j CB](resilience4j-interview.md)). Подходит для простых случаев.

```java
CircuitBreakerRetryPolicy circuitBreakerPolicy = new CircuitBreakerRetryPolicy(
    new SimpleRetryPolicy(3)
);
circuitBreakerPolicy.setOpenTimeout(5000);    // цепь открыта 5 сек
circuitBreakerPolicy.setResetTimeout(20000);  // сбросить через 20 сек

RetryTemplate template = new RetryTemplate();
template.setRetryPolicy(circuitBreakerPolicy);
```

**Ограничения Spring Retry CB:** нет метрик, нет HALF_OPEN состояния, нет Spring Boot auto-configuration. Для production используй [Resilience4j](resilience4j-interview.md).

## Q9. (!) Почему @Retryable не работает при self-invocation?

`@Retryable` работает через **Spring AOP proxy**: прокси обёртывает bean снаружи, а вызов `this.method()` обходит прокси и попадает напрямую в реальный объект — retry не срабатывает.

Это та же проблема, что с `@Transactional`. Подробнее в [Spring AOP](spring-aop-interview.md).

```java
@Service
public class PaymentService {

    // Self-invocation — retry НЕ работает
    public void process(Payment p) {
        retryableMethod(p);  // this.retryableMethod() — обходит proxy!
    }

    @Retryable(retryFor = IOException.class)
    public void retryableMethod(Payment p) {
        gateway.charge(p);
    }
}
```

**Решения:**

1. **Вынести в отдельный bean (рекомендуется):**
```java
@Service
public class PaymentRetryService {
    @Retryable(retryFor = IOException.class)
    public void charge(Payment p) { ... }
}
```

2. **Self-inject через `@Lazy`:**
```java
@Service
public class PaymentService {
    @Autowired @Lazy
    private PaymentService self;

    public void process(Payment p) {
        self.retryableMethod(p); // через proxy
    }
}
```

3. **RetryTemplate** — не зависит от AOP.

## Q10. (!) Как правильно комбинировать @Retryable и @Transactional?

Если метод **одновременно** `@Transactional` и `@Retryable`, транзакция откатывается до того, как retry срабатывает — и retry бесполезен.

**Неправильно:**
```java
@Transactional
@Retryable(retryFor = OptimisticLockException.class)
public void updateWithOptimisticLock(Entity e) {
    // OptimisticLockException → rollback → retry запускается
    // → но транзакция уже откатилась и объект в dirty state
    repository.save(e);
}
```

**Правильно — retry снаружи, транзакция внутри:**
```java
@Retryable(retryFor = OptimisticLockException.class, maxAttempts = 3)
public void updateWithRetry(Entity e) {
    doUpdateTransactional(e);  // каждая попытка = новая транзакция
}

@Transactional
public void doUpdateTransactional(Entity e) {
    repository.save(e);
}
```

**Правило:** `@Retryable` должен быть на **внешнем** методе (без `@Transactional`), а транзакция — во вложенном.

## Q11. Как Spring Retry интегрируется с Reactor/WebClient?

`@Retryable` **не работает** с реактивными методами (`Mono`/`Flux`) — для них нужен встроенный Reactor-механизм:

```java
webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(Data.class)
    .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
        .filter(e -> e instanceof WebClientResponseException.ServiceUnavailable)
        .jitter(0.5)
        .onRetryExhaustedThrow((spec, signal) ->
            new ServiceUnavailableException("All retries exhausted")));
```

**`Retry.backoff()`** — Reactor-native retry с экспоненциальным backoff и jitter.

Альтернатива для WebClient — Resilience4j `ReactiveCircuitBreaker` + `ReactiveRateLimiter`.

## Q12. Что такое Stateful Retry и когда он нужен?

`Stateful Retry` сохраняет состояние между попытками через ключ (`RetryState`). Нужен для **транзакционных message listeners** (Kafka, JMS).

**Проблема:** в Kafka listener при ошибке транзакция откатывается, сообщение возвращается в очередь, consumer получает его снова — это уже новый вызов, не continuation предыдущего retry.

```java
// RetryTemplate с stateful retry для Kafka
RetryTemplate template = new RetryTemplate();
template.setRetryPolicy(new SimpleRetryPolicy(3));

// RetryStateGenerator идентифицирует конкретное сообщение
template.setRetryState(ctx -> new DefaultRetryState(messageKey));

template.execute(ctx -> {
    processMessage(message);
    return null;
});
```

В `spring-kafka` stateful retry настраивается через `SeekToCurrentErrorHandler` + `BackOff`.

## Q13. Как добавить метрики и логирование через RetryListener?

`RetryListener` позволяет перехватывать события retry:

```java
@Bean
public RetryTemplate retryTemplate(MeterRegistry meterRegistry) {
    RetryTemplate template = RetryTemplate.builder()
        .maxAttempts(3)
        .build();

    template.registerListener(new RetryListenerSupport() {
        @Override
        public <T, E extends Throwable> void onError(
                RetryContext ctx, RetryCallback<T, E> cb, Throwable t) {
            String method = ctx.getAttribute(RetryContext.NAME).toString();
            meterRegistry.counter("retry.attempts", "method", method).increment();
            log.warn("Retry attempt {} for {}: {}", ctx.getRetryCount(), method, t.getMessage());
        }

        @Override
        public <T, E extends Throwable> void close(
                RetryContext ctx, RetryCallback<T, E> cb, Throwable t) {
            if (ctx.getRetryCount() > 0) {
                log.info("Retry completed after {} attempts for {}",
                    ctx.getRetryCount(), ctx.getAttribute(RetryContext.NAME));
            }
        }
    });
    return template;
}
```

## Q14. Как настроить разные retry-политики для разных методов?

```java
@Retryable(
    retryFor = IOException.class,
    maxAttempts = 5,
    backoff = @Backoff(delay = 2000, multiplier = 1.5)
)
public void syncData() { ... }

@Retryable(
    retryFor = {ServiceUnavailableException.class},
    maxAttempts = 2,
    backoff = @Backoff(delay = 500)
)
public QuoteResponse getQuote() { ... }
```

**Класс-уровень @Retryable** как default:
```java
@Service
@Retryable(retryFor = IOException.class, maxAttempts = 3)
public class ExternalApiService {

    public Data fetchData() { ... }      // унаследует class-level retry

    @Retryable(maxAttempts = 5)          // переопределяет
    public Data fetchCriticalData() { ... }
}
```

## Q15. Что произойдёт, если @Recover выбросит исключение?

Если `@Recover` метод сам выбрасывает исключение — оно **передаётся вызывающему коду** как обычное исключение. Spring Retry **не делает** повторных попыток для исключений из `@Recover`.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    // Если это выбросит, исключение дойдёт до caller без retry
    return fallbackService.getFallbackResult(req);
}
```

**Итог:** `@Recover` — терминальный handler. Обработай все случаи или явно пробрось нужное исключение.

## Q16. Как протестировать логику retry в тестах?

`@Retryable` работает через Spring AOP — нужен `@SpringBootTest` или `@SpringJUnitConfig` с AOP. Обычный `new MyService()` обойдёт прокси.

```java
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @MockBean
    PaymentGateway gateway;

    @Test
    void shouldRetry3TimesOnIOException() {
        when(gateway.charge(any())).thenThrow(new IOException("timeout"));

        assertThatThrownBy(() -> paymentService.processPayment(request))
            .isInstanceOf(IOException.class);

        verify(gateway, times(3)).charge(any()); // проверяем 3 попытки
    }

    @Test
    void shouldCallRecoverAfterExhaustion() {
        when(gateway.charge(any())).thenThrow(new IOException());

        PaymentResult result = paymentService.processPayment(request);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo("Service unavailable");
    }
}
```

**Ускорение тестов:** для тестирования retry логики без реального backoff используй `RetryTemplate` напрямую без `@Backoff`.

## Q17. Чем Spring Retry отличается от Resilience4j?

| Критерий | Spring Retry | Resilience4j |
|---|---|---|
| Retry | Полноценный | Полноценный |
| Circuit Breaker | Базовый (без HALF_OPEN) | Полноценный (CLOSED/OPEN/HALF_OPEN) |
| Rate Limiter | Нет | Есть |
| Bulkhead | Нет | Есть (ThreadPool + Semaphore) |
| TimeLimiter | Нет | Есть |
| Реактивный стек | Нет | Reactor, RxJava |
| Метрики Micrometer | Через listener вручную | Встроенные out-of-the-box |
| Spring Boot auto-config | Частичная | Полная (через starter) |

**Когда Spring Retry:** простые сценарии retry, уже используешь Spring Batch/Integration (они используют Spring Retry внутри), не нужен CB.

**Когда Resilience4j:** production microservices, нужен Circuit Breaker + метрики + observability. Подробнее в [Resilience4j](resilience4j-interview.md).

---

## See also

- [Resilience4j](resilience4j-interview.md) — полноценная fault tolerance: CB, RateLimiter, Bulkhead
- [Spring @Transactional](spring-transaction-interview.md) — совместное использование с @Retryable
- [Spring AOP](spring-aop-interview.md) — механизм proxy, self-invocation проблема
- [Spring WebFlux](spring-webflux-interview.md) — реактивный retry через Reactor
- [Spring Testing](spring-testing-interview.md) — тестирование @Retryable в @SpringBootTest
- [Spring Batch](spring-batch-interview.md) — Spring Retry встроен в retry step
- [Micrometer](../../monitoring/micrometer-interview.md) — метрики retry через RetryListener + MeterRegistry
- [Distributed Systems](../../architecture/distributed-systems-interview.md) — теория: retry паттерны, idempotency
