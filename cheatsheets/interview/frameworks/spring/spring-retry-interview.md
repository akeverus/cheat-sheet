---
title: "Spring Retry — Interview"
description: "Вопросы на собеседовании по Spring Retry: @Retryable, @Recover, RetryTemplate, backoff-стратегии, типичные ошибки."
tags:
  - interview
  - spring
  - retry
  - resilience
difficulty: "intermediate"
updated: "2026-04-20"
---
# Spring Retry — Interview

## Q1. Что такое Spring Retry и для каких задач он предназначен?

Spring Retry — библиотека для автоматического повтора операций при сбоях. Применяется для обращений к внешним API, сетевых вызовов, операций с БД при дедлоках, чтения из очередей.

Основной принцип: при получении заданного исключения метод вызывается повторно заданное число раз с настраиваемой паузой между попытками.

## Q2. Как подключить Spring Retry и что нужно для работы аннотаций?

```xml
<dependency>
  <groupId>org.springframework.retry</groupId>
  <artifactId>spring-retry</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework</groupId>
  <artifactId>spring-aspects</artifactId>
</dependency>
```

Аннотационный подход требует AOP: нужно добавить `@EnableRetry` на конфигурационный класс.

```java
@Configuration
@EnableRetry
public class AppConfig {}
```

## Q3. Как работает @Retryable? Покажите пример с параметрами.

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

- `retryFor` — исключения, при которых повторять.
- `noRetryFor` — исключения, при которых НЕ повторять (обычно business-exceptions).
- `maxAttempts` — максимальное число попыток (включая первую).
- `backoff` — задержка между попытками.

## Q4. Что такое @Recover и каковы требования к сигнатуре метода?

`@Recover` — метод-fallback, вызывается после исчерпания всех попыток.

```java
@Recover
public PaymentResult recoverPayment(IOException e, PaymentRequest req) {
    log.error("Payment failed after retries for {}", req.getId(), e);
    return PaymentResult.failed("Service unavailable");
}
```

Требования:
- **Тот же класс**, что и `@Retryable`.
- **Первый аргумент** — тип пойманного исключения (или суперкласс).
- **Тот же возвращаемый тип**, что и у `@Retryable`.
- Spring выбирает наиболее специфичный `@Recover` по типу исключения.

## Q5. Какие backoff-стратегии есть в Spring Retry?

```java
// Фиксированная задержка
@Backoff(delay = 2000)                          // 2 сек

// Экспоненциальная
@Backoff(delay = 1000, multiplier = 2.0)        // 1s → 2s → 4s

// Экспоненциальная с потолком
@Backoff(delay = 1000, multiplier = 2.0, maxDelay = 10000)

// С джиттером (предотвращает thundering herd)
@Backoff(delay = 1000, multiplier = 2.0, random = true)
```

**Thundering herd** — ситуация, когда все клиенты одновременно повторяют запросы после сбоя сервиса. Джиттер случайно разбрасывает попытки во времени.

## Q6. Что такое RetryTemplate и когда его использовать?

`RetryTemplate` — программный API для retry без аннотаций. Используют, когда:
- Нужен retry не в Spring bean (utility-метод, лямбда).
- Retry-политика должна конфигурироваться динамически.
- Нужен явный recover-callback прямо в коде.

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
retryTemplate.execute(
    ctx -> externalApi.call(data),
    ctx -> fallback(data)
);
```

## Q7. Почему @Retryable не работает при вызове метода внутри того же класса?

Self-invocation — это проблема Spring AOP-прокси: прокси обёртывает бин снаружи, а вызов `this.method()` обходит прокси и попадает напрямую в реальный объект.

**Решения:**
1. Вынести метод с `@Retryable` в отдельный бин.
2. Внедрить self-reference через `@Autowired ApplicationContext` и вызвать через него.
3. Использовать `RetryTemplate` — не зависит от AOP.

## Q8. Как правильно применять retry к транзакционным методам?

Если метод одновременно `@Transactional` и `@Retryable`, транзакция откатывается до того, как retry срабатывает. Нужно вынести retry *снаружи* транзакции:

```java
// Неправильно — транзакция откатывается, retry не поможет
@Transactional
@Retryable(retryFor = OptimisticLockException.class)
public void update(Entity e) { ... }

// Правильно — retry снаружи, @Transactional внутри
@Retryable(retryFor = OptimisticLockException.class)
public void updateWithRetry(Entity e) {
    doUpdateTransactional(e);
}

@Transactional
public void doUpdateTransactional(Entity e) { ... }
```

## Q9. Что такое Stateful Retry и когда он нужен?

Stateful retry сохраняет состояние между попытками через `RetryContext`. Нужен для операций с очередями (JMS, Kafka): если та же транзакция получает то же сообщение снова, retry не должен считаться новым.

```java
SimpleRetryPolicy policy = new SimpleRetryPolicy(3,
    Map.of(MessagingException.class, true));

RetryTemplate template = new RetryTemplate();
template.setRetryPolicy(policy);
// + установить RetryStateGenerator для идентификации транзакции
```

## Q10. Как Spring Retry интегрируется с Reactor/WebClient?

Spring Retry (`@Retryable`) не работает с реактивными методами — для `Mono`/`Flux` нужен встроенный Reactor-механизм:

```java
webClient.get().uri("/api/data")
    .retrieve()
    .bodyToMono(Data.class)
    .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
        .filter(e -> e instanceof WebClientResponseException.ServiceUnavailable)
        .onRetryExhaustedThrow((spec, signal) ->
            new RuntimeException("Service unavailable after retries")));
```

## Q11. Как добавить метрики и логирование в RetryTemplate?

```java
template.registerListener(new RetryListenerSupport() {
    @Override
    public <T, E extends Throwable> void onError(
            RetryContext ctx, RetryCallback<T, E> cb, Throwable t) {
        meterRegistry.counter("retry.attempts",
            "method", ctx.getAttribute(RetryContext.NAME).toString()).increment();
        log.warn("Retry attempt {} for {}",
            ctx.getRetryCount(), ctx.getAttribute(RetryContext.NAME));
    }
});
```

## Q12. В чём разница между Spring Retry и Resilience4j?

| Критерий | Spring Retry | Resilience4j |
|----------|-------------|--------------|
| Retry | Да | Да |
| Circuit Breaker | Базовый | Полноценный (CLOSED/OPEN/HALF_OPEN) |
| Bulkhead | Нет | Да (ThreadPool + Semaphore) |
| Rate Limiter | Нет | Да |
| Реактивный стек | Нет | Да (Reactor, RxJava) |
| Метрики | Через listener | Встроенные Micrometer |

Spring Retry — простой и достаточный для большинства сценариев. Resilience4j — полноценная библиотека устойчивости для production с богатым мониторингом.

## Q13. Как настроить разные retry-политики для разных методов?

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

Каждый метод настраивается независимо. Для общей конфигурации — `@Retryable` на уровне класса как default.

## Q14. Что произойдёт, если @Recover метод выбросит исключение?

Если `@Recover` метод сам выбрасывает исключение, оно передаётся вызывающему коду как обычное исключение. Spring Retry не делает повторных попыток для исключений из `@Recover`.

## Q15. Как протестировать логику retry в юнит-тестах?

```java
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    PaymentService paymentService;

    @MockBean
    PaymentGateway gateway;

    @Test
    void shouldRetry3Times() {
        when(gateway.charge(any())).thenThrow(new IOException("timeout"));

        assertThatThrownBy(() -> paymentService.processPayment(request))
            .isInstanceOf(IOException.class);

        // verify 3 attempts
        verify(gateway, times(3)).charge(any());
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

**Важно:** `@SpringBootTest` нужен, т.к. `@Retryable` работает через Spring AOP. Простой `new PaymentService()` обойдёт прокси.

## See also

- [[spring-retry|Spring Retry]] — полный cheatsheet
- [[java-resilience4j|Resilience4j]] — полноценная библиотека устойчивости
- [[spring-webflux-interview|Spring WebFlux Interview]] — реактивный retry
- [[spring-boot-interview|Spring Boot Interview]] — общие вопросы Spring Boot
- [[spring-kafka|Spring Kafka]] — retry в Kafka listeners
