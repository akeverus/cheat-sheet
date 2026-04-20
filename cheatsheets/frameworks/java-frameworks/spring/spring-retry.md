---
title: "Spring Retry — повторные попытки операций"
description: "Spring Retry: @Retryable, @Recover, RetryTemplate, backoff-стратегии, stateful retry, интеграция со Spring Batch и WebClient."
tags:
  - frameworks
  - spring
  - retry
  - resilience
  - fault-tolerance
difficulty: "intermediate"
updated: "2026-04-20"
---
# Spring Retry — повторные попытки операций

Spring Retry автоматически повторяет выполнение метода при определённых исключениях.

## Зависимость

```xml
<dependency>
  <groupId>org.springframework.retry</groupId>
  <artifactId>spring-retry</artifactId>
</dependency>
<!-- Для аннотационного подхода нужен AOP -->
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-aspects</artifactId>
</dependency>
```

```java
@Configuration
@EnableRetry
public class RetryConfig {}
```

## @Retryable

```java
@Service
public class PaymentService {

    // Повторить до 3 раз при IOException, пауза 1 сек
    @Retryable(
        retryFor = {IOException.class, TimeoutException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public PaymentResult processPayment(PaymentRequest request) {
        return externalGateway.charge(request);
    }

    // Метод восстановления при исчерпании попыток
    @Recover
    public PaymentResult recoverPayment(IOException e, PaymentRequest request) {
        log.error("Payment failed after retries: {}", request.getId(), e);
        return PaymentResult.failed("Service unavailable");
    }
}
```

**Важно:** `@Recover`-метод должен быть в том же классе, иметь тот же возвращаемый тип и первым аргументом принимать исключение.

## Backoff-стратегии

```java
// Фиксированная задержка
@Backoff(delay = 2000)                         // 2 сек между попытками

// Экспоненциальная с мультипликатором
@Backoff(delay = 1000, multiplier = 2.0)       // 1s → 2s → 4s → 8s

// Экспоненциальная с ограничением
@Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)  // не более 10s

// С джиттером (случайный разброс) — предотвращает thundering herd
@Backoff(delay = 1000, multiplier = 2.0, random = true)
```

## Исключение конкретных исключений

```java
@Retryable(
    retryFor = Exception.class,
    noRetryFor = {ValidationException.class, BusinessException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 500)
)
public void processOrder(Order order) { ... }
```

## RetryTemplate (программный подход)

```java
@Bean
public RetryTemplate retryTemplate() {
    return RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(1000, 2, 10000)
        .retryOn(IOException.class)
        .build();
}

// Использование
@Service
@RequiredArgsConstructor
public class DataSyncService {
    private final RetryTemplate retryTemplate;

    public void sync(Data data) {
        retryTemplate.execute(
            context -> {
                externalApi.push(data);
                return null;
            },
            context -> {
                // recover при исчерпании
                log.warn("Sync failed, using fallback");
                fallbackQueue.enqueue(data);
                return null;
            }
        );
    }
}
```

## Stateful Retry

Для операций с состоянием (например, JMS) — гарантирует, что одна и та же транзакция не повторяется:

```java
@Bean
public RetryTemplate statefulRetryTemplate() {
    Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
    retryableExceptions.put(MessagingException.class, true);

    SimpleRetryPolicy policy = new SimpleRetryPolicy(3, retryableExceptions);

    RetryTemplate template = new RetryTemplate();
    template.setRetryPolicy(policy);

    return template;
}
```

## CircuitBreaker через Spring Retry

```java
@Bean
public CircuitBreakerRetryPolicy circuitBreakerPolicy() {
    SimpleRetryPolicy delegate = new SimpleRetryPolicy(3);
    return new CircuitBreakerRetryPolicy(delegate);
}
```

Для production circuit breaker рекомендуется [[java-resilience4j|Resilience4j]].

## Retry с WebClient (реактивный стек)

```java
webClient.get()
    .uri("/api/data")
    .retrieve()
    .bodyToMono(Data.class)
    .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
        .filter(e -> e instanceof WebClientResponseException.ServiceUnavailable)
        .onRetryExhaustedThrow((spec, signal) ->
            new RuntimeException("Service unavailable after retries")));
```

## Метрики и логирование

```java
@Bean
public RetryTemplate retryTemplate(MeterRegistry meterRegistry) {
    RetryTemplate template = RetryTemplate.builder()
        .maxAttempts(3)
        .exponentialBackoff(500, 2, 5000)
        .build();

    // Слушатель для метрик
    template.registerListener(new RetryListenerSupport() {
        @Override
        public <T, E extends Throwable> void onError(
                RetryContext ctx, RetryCallback<T, E> callback, Throwable t) {
            meterRegistry.counter("retry.attempts",
                "method", ctx.getAttribute(RetryContext.NAME).toString()).increment();
            log.warn("Retry attempt {} for {}", ctx.getRetryCount(),
                ctx.getAttribute(RetryContext.NAME));
        }
    });
    return template;
}
```

## Типичные ошибки

| Проблема | Причина | Решение |
|----------|---------|---------|
| Retry не срабатывает | Метод вызывается внутри того же bean (self-invocation) | Вынести в отдельный bean или внедрить self-reference |
| `@Recover` не вызывается | Сигнатура не совпадает с `@Retryable` | Первый аргумент = тип исключения, тот же возвращаемый тип |
| Retry на транзакционный метод | Транзакция откатывается до retry | Вынести retry снаружи `@Transactional` или использовать REQUIRES_NEW |
| Бесконечный retry loop | `maxAttempts` не ограничен | Всегда задавать `maxAttempts` |

## See also

- [[spring-boot|Spring Boot]]
- [[java-resilience4j|Resilience4j]]
- [[spring-webflux|Spring WebFlux]]
- [[spring-kafka|Spring Kafka]]
- [[spring-batch|Spring Batch]]
