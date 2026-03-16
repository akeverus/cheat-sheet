---
title: "Повтор с экспоненциальной задержкой и джиттером (Retry with Exponential Backoff and Jitter)"
description: "Повторы с растущими паузами (exponential backoff) и джиттером снижают нагрузку на сервис и предотвращают «стадный» эффект синхронных повторов в распределённых системах."
tags: ["algorithms", "problems", "retry-with-exponential-backoff-jitter"]
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Повтор с экспоненциальной задержкой и джиттером (`Retry with Exponential Backoff and Jitter`)

**Дата последнего обновления:** 2026-02-06

Повторы с растущими паузами (exponential backoff) и джиттером снижают нагрузку на сервис и предотвращают «стадный» эффект синхронных повторов в распределённых системах.



## Полезные ссылки

- [Resilience4j Retry](https://resilience4j.readme.io/docs/retry)
- [Exponential Backoff and Jitter (AWS)](https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/)
- [Паттерны проектирования](../../patterns/)

## Содержание

- [Обзор](#обзор)
- [Проблема простых повторов](#проблема-простых-повторов)
- [Exponential Backoff](#exponential-backoff)
- [Реализация на Java](#реализация-на-java)
- [Thundering Herd и джиттер](#thundering-herd-и-джиттер)
- [Resilience4j Implementation](#resilience4j-implementation)
- [Полный пример](#полный-пример)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Резюме](#резюме)
- [Реализация на Kotlin](#реализация-на-kotlin)

## Обзор

В распределённых системах сетевые сбои неизбежны; клиенты обрабатывают их повторными вызовами. Ниже — как улучшить повторы за счёт экспоненциальной задержки и джиттера. Интерфейс удалённого сервиса (вызов может бросить исключение при сбое):

```java
// Интерфейс удалённого сервиса: вызов может выбросить исключение при сбое
interface PingPongService {
    String call(String ping) throws PingPongServiceException;
}
```

Клиенту стоит повторять вызов при `PingPongServiceException`. Далее — настройка повторов с backoff и jitter.

## Проблема простых повторов

**Client applications must** be **responsible about retries**. **When clients retry failed calls without waiting**, **they can overload the system and contribute** to **further degradation** of a **service that**'s **already** in a **failed state**.

## Exponential Backoff

**Exponential backoff** is a **common strategy for handling retries** of **failed network calls**. **Simply put**, **clients wait for gradually increasing intervals between consecutive retries**:**

```text
wait_interval = base * multiplier^n
```

**where**:

- **base** - **initial interval**, **i.e**., **wait for the first retry**
- **n** - **number** of **failures that have occurred**
- **multiplier** - **arbitrary multiplier that can** be **replaced with any suitable value**

**With this approach**, we **give the system** a **breather** to **recover from periodic failures** or **even more serious problems**.

## Реализация на Java

### Implementing Exponential Backoff

**In **our example**, we'll **use the Resilience4j library**, **specifically its retry module**. We **need** to **add the resilience4j-retry module** to **our pom.xml**:**

```xml
<!-- resilience4j-retry: повторы с exponential backoff и jitter -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-retry</artifactId>
    <version>1.7.1</version>
</dependency>
```

We **can use the exponential backoff algorithm** in **Resilience4j retry** by **configuring its IntervalFunction**, **which takes** an **initial interval and multiplier**.

**IntervalFunction** is **used** by **the retry mechanism** as a **wait function**:**

```java
IntervalFunction intervalFn = IntervalFunction.ofExponentialBackoff(INITIAL_INTERVAL, MULTIPLIER);

RetryConfig retryConfig = RetryConfig.custom()
    .maxAttempts(MAX_RETRIES)
    .intervalFunction(intervalFn)
    .build();

Retry retry = Retry.of("pingpong", retryConfig);

Function<String, String> pingPongFn = Retry
    .decorateFunction(retry, ping -> service.call(ping));

pingPongFn.apply("Hello");
```

## Thundering Herd и джиттер

**Let**'s **simulate** a **real scenario and assume** we **have several clients simultaneously calling PingPongService**:**

```java
ExecutorService executors = Executors.newFixedThreadPool(NUM_CONCURRENT_CLIENTS);
List<Callable<String>> tasks = Collections.nCopies(NUM_CONCURRENT_CLIENTS, 
    () -> pingPongFn.apply("Hello"));
executors.invokeAll(tasks);
```

**Let**'s **look** at **the remote call logs for NUM_CONCURRENT_CLIENTS equal** `to 4`:**

```text
[thread-1] At 00:37:42.756
[thread-2] At 00:37:42.756
[thread-3] At 00:37:42.756
[thread-4] At 00:37:42.756
[thread-2] At 00:37:43.802
[thread-4] At 00:37:43.802
[thread-1] At 00:37:43.802
[thread-3] At 00:37:43.802
[thread-2] At 00:37:45.803
[thread-1] At 00:37:45.803
[thread-4] At 00:37:45.803
[thread-3] At 00:37:45.803
[thread-2] At 00:37:49.808
[thread-3] At 00:37:49.808
[thread-4] At 00:37:49.808
[thread-1] At 00:37:49.808
```

**Here** we **see** a **clear pattern** - **clients wait for exponentially growing intervals**, **but they all call the remote service** at **exactly the same time** on **each retry** (**collisions**).

We've **only solved part** of **the problem** - we're no **longer hammering the remote service with retries**, **but instead** of **distributing the load over time**, we **have alternating periods** of **work with large idle times**. **This behavior** is **akin** to **the thundering herd problem**.

### Добавление джиттера

**Jitter** is a **random variation added** to **the backoff interval** to **prevent synchronized retries**. **This helps distribute retry attempts over time**, **reducing the thundering herd effect**.

### Full Jitter

**Full jitter adds** a **random value between** 0 **and the calculated backoff interval**:**

```java
wait_interval = random(0, base * multiplier^n)
```

### Equal Jitter

**Equal jitter adds** a **random value** to **half** of **the backoff interval**:**

```java
wait_interval = (base * multiplier^n) / 2 + random(0, (base * multiplier^n) / 2)
```

### Decorrelated Jitter

**Decorrelated jitter uses** a **more sophisticated approach that decorrelates retry attempts**:**

```java
wait_interval = random(base, previous_wait_interval * 3)
```

## Resilience4j Implementation

**Resilience4j supports jitter through its IntervalFunction**. **Here**'s **how** to **add jitter**:**

```java
IntervalFunction intervalFn = IntervalFunction.ofExponentialRandomBackoff(
    INITIAL_INTERVAL, 
    MULTIPLIER, 
    RANDOMIZATION_FACTOR
);
```

**Where** `**RANDOMIZATION_FACTOR**` is a **value between** 0 **and** 1 **that determines the amount** of **jitter**.

## Полный пример

**Here**'s a **complete example demonstrating exponential backoff with jitter**:**

```java
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.core.IntervalFunction;

import java.time.LocalDateTime;
import java.util.function.Function;

public class RetryWithBackoffExample {
    
    private static final long INITIAL_INTERVAL = 1000; // 1 second
    private static final double MULTIPLIER = 2.0;
    private static final double RANDOMIZATION_FACTOR = 0.5; // 50% jitter
    private static final int MAX_RETRIES = 5;
    
    public static void main(String[] args) {
        // Create interval function with exponential backoff and jitter
        IntervalFunction intervalFn = IntervalFunction.ofExponentialRandomBackoff(
            INITIAL_INTERVAL,
            MULTIPLIER,
            RANDOMIZATION_FACTOR
        );
        
        // Configure retry
        RetryConfig retryConfig = RetryConfig.custom()
            .maxAttempts(MAX_RETRIES)
            .intervalFunction(intervalFn)
            .retryOnException(exception -> exception instanceof PingPongServiceException)
            .build();
        
        Retry retry = Retry.of("pingpong", retryConfig);
        
        // Create service
        PingPongService service = new PingPongService();
        
        // Decorate function with retry
        Function<String, String> pingPongFn = Retry
            .decorateFunction(retry, ping -> {
                System.out.println("[" + Thread.currentThread().getName() + 
                    "] Calling service at " + LocalDateTime.now());
                return service.call(ping);
            });
        
        // Test with multiple concurrent calls
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    pingPongFn.apply("Hello from thread " + threadNum);
                } catch (Exception e) {
                    System.err.println("Thread " + threadNum + " failed: " + e.getMessage());
                }
            });
        }
        
        executor.shutdown();
    }
    
    static class PingPongService {
        private int attemptCount = 0;
        
        public String call(String ping) throws PingPongServiceException {
            attemptCount++;
            if (attemptCount < 3) {
                throw new PingPongServiceException("Service temporarily unavailable");
            }
            return "Pong: " + ping;
        }
    }
    
    static class PingPongServiceException extends Exception {
        public PingPongServiceException(String message) {
            super(message);
        }
    }
}
```

### Custom Jitter Implementation

**If **you need more control**, **you can implement custom jitter**:**

```java
public class CustomRetryWithJitter {
    
    private static final long INITIAL_INTERVAL = 1000;
    private static final double MULTIPLIER = 2.0;
    private static final Random random = new Random();
    
    public static long calculateBackoffWithJitter(int attemptNumber) {
        long baseInterval = (long) (INITIAL_INTERVAL * Math.pow(MULTIPLIER, attemptNumber));
        
        // Full jitter: random between 0 and baseInterval
        return random.nextLong(baseInterval);
        
        // Or equal jitter:
        // long halfInterval = baseInterval / 2;
        // return halfInterval + random.nextLong(halfInterval);
    }
    
    public static <T> T retryWithBackoff(Supplier<T> operation, int maxRetries) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                return operation.get();
            } catch (Exception e) {
                if (attempt == maxRetries - 1) {
                    throw new RuntimeException("Max retries exceeded", e);
                }
                
                long backoff = calculateBackoffWithJitter(attempt);
                System.out.println("Attempt " + (attempt + 1) + " failed. Retrying in " + 
                    backoff + "ms");
                
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during backoff", ie);
                }
            }
        }
        throw new RuntimeException("Should not reach here");
    }
}
```

## Лучшие практики

Используйте exponential backoff для временных сбоев и добавляйте джиттер, чтобы избежать синхронных повторов многих клиентов. Ограничивайте максимальное число попыток, логируйте повторы для отладки и мониторинга; при устойчивых сбоях рассмотрите circuit breaker. Выбор джиттера: full jitter — при высокой конкуренции, equal jitter — более предсказуемые паузы, decorrelated jitter — для распределённых систем.

## Решение проблем

| Симптом | Возможная причина | Решение |
|--------|-------------------|---------|
| Все клиенты повторяют в один момент | Нет джиттера | Включить `IntervalFunction.ofExponentialRandomBackoff` с `randomizationFactor` |
| Сервис не успевает восстановиться | Слишком малая начальная пауза или множитель | Увеличить `initialInterval` и/или `multiplier` |
| Бесконечные повторы | Нет лимита попыток | Задать `maxAttempts` в `RetryConfig` |

## Частые вопросы

**Когда использовать full vs equal jitter?** Full jitter даёт большую размазанность по времени и хорошо снижает пики при большом числе клиентов; equal jitter сохраняет нижнюю границу паузы (половина интервала), что удобно, когда нужна предсказуемость.

**Нужен ли circuit breaker вместе с retry?** При повторяющихся устойчивых сбоях — да: retry обрабатывает кратковременные сбои, circuit breaker отключает вызовы к «упавшему» сервису и даёт ему время на восстановление.

## Резюме

Exponential backoff снижает нагрузку на восстанавливающийся сервис; джиттер устраняет эффект «стада» при одновременных повторах. В Resilience4j для этого достаточно настроить `IntervalFunction.ofExponentialRandomBackoff` и лимит попыток.

## Реализация на Kotlin

### Exponential Backoff with Resilience4j

```kotlin
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import io.github.resilience4j.core.IntervalFunction
import java.time.LocalDateTime
import java.util.function.Function

object RetryWithBackoffExampleK {
    private const val INITIAL_INTERVAL = 1000L // 1 second
    private const val MULTIPLIER = 2.0
    private const val RANDOMIZATION_FACTOR = 0.5 // 50% jitter
    private const val MAX_RETRIES = 5
    
    fun createRetryWithBackoff(service: PingPongServiceK): Function<String, String> {
        val intervalFn = IntervalFunction.ofExponentialRandomBackoff(
            INITIAL_INTERVAL,
            MULTIPLIER,
            RANDOMIZATION_FACTOR
        )
        
        val retryConfig = RetryConfig.custom<String>()
            .maxAttempts(MAX_RETRIES)
            .intervalFunction(intervalFn)
            .retryOnException { it is PingPongServiceExceptionK }
            .build()
        
        val retry = Retry.of("pingpong", retryConfig)
        
        return Retry.decorateFunction(retry) { ping ->
            println("${LocalDateTime.now()}: Calling service with: $ping")
            service.call(ping)
        }
    }
}

interface PingPongServiceK {
    fun call(ping: String): String
}

class PingPongServiceExceptionK(message: String) : Exception(message)
```

### Пример использования

```kotlin
fun main() {
    val service = object : PingPongServiceK {
        private var attemptCount = 0
        
        override fun call(ping: String): String {
            attemptCount++
            if (attemptCount < 3) {
                throw PingPongServiceExceptionK("Service unavailable")
            }
            return "Pong: $ping"
        }
    }
    
    val retryFunction = RetryWithBackoffExampleK.createRetryWithBackoff(service)
    val result = retryFunction.apply("Hello")
    println("Result: $result")
}
```

