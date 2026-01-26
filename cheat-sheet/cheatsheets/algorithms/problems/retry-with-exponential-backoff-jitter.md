# Retry with Exponential Backoff and Jitter

A guide to improving client retries using exponential backoff and jitter strategies to prevent thundering herd problems in distributed systems.

**Last Updated:** 2025-01-15

## Useful Links

### Official Documentation
- [Resilience4j Retry Module](https://resilience4j.readme.io/docs/retry)
- [Exponential Backoff - AWS Best Practices](https://docs.aws.amazon.com/general/latest/gr/api-retries.html)

### See Also
- [Error Handling Patterns](../../patterns/)
- [Concurrent Programming](../ai-collections/)

## Table of Contents

- [Overview](#overview)
- [The Problem with Simple Retries](#the-problem-with-simple-retries)
- [Exponential Backoff](#exponential-backoff)
- [Java Implementation](#java-implementation)
- [Kotlin Implementation](#kotlin-implementation)
- [The Thundering Herd Problem](#the-thundering-herd-problem)
- [Adding Jitter](#adding-jitter)
- [Resilience4j Implementation](#resilience4j-implementation)
- [Complete Example](#complete-example)

## Overview

In this tutorial, we'll look at how we can improve client retries using two different strategies: exponential backoff and jitter.

In a distributed system, network communication between numerous components can be disrupted at any time. Client applications handle these failures by implementing retries.

Suppose we have a client application that calls a remote service - PingPongService:

```java
interface PingPongService {
    String call(String ping) throws PingPongServiceException;
}
```

The client application should retry if PingPongService returns PingPongServiceException. In the following sections, we'll look at ways to implement client retries.

## The Problem with Simple Retries

Client applications must be responsible about retries. When clients retry failed calls without waiting, they can overload the system and contribute to further degradation of a service that's already in a failed state.

## Exponential Backoff

Exponential backoff is a common strategy for handling retries of failed network calls. Simply put, clients wait for gradually increasing intervals between consecutive retries:

```
wait_interval = base * multiplier^n
```

where:

- **base** - initial interval, i.e., wait for the first retry
- **n** - number of failures that have occurred
- **multiplier** - arbitrary multiplier that can be replaced with any suitable value

With this approach, we give the system a breather to recover from periodic failures or even more serious problems.

## Java Implementation

### Implementing Exponential Backoff

In our example, we'll use the Resilience4j library, specifically its retry module. We need to add the resilience4j-retry module to our pom.xml:

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-retry</artifactId>
    <version>1.7.1</version>
</dependency>
```

We can use the exponential backoff algorithm in Resilience4j retry by configuring its IntervalFunction, which takes an initial interval and multiplier.

IntervalFunction is used by the retry mechanism as a wait function:

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

## The Thundering Herd Problem

Let's simulate a real scenario and assume we have several clients simultaneously calling PingPongService:

```java
ExecutorService executors = Executors.newFixedThreadPool(NUM_CONCURRENT_CLIENTS);
List<Callable<String>> tasks = Collections.nCopies(NUM_CONCURRENT_CLIENTS, 
    () -> pingPongFn.apply("Hello"));
executors.invokeAll(tasks);
```

Let's look at the remote call logs for NUM_CONCURRENT_CLIENTS equal to 4:

```
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

Here we see a clear pattern - clients wait for exponentially growing intervals, but they all call the remote service at exactly the same time on each retry (collisions).

We've only solved part of the problem - we're no longer hammering the remote service with retries, but instead of distributing the load over time, we have alternating periods of work with large idle times. This behavior is akin to the thundering herd problem.

## Adding Jitter

Jitter is a random variation added to the backoff interval to prevent synchronized retries. This helps distribute retry attempts over time, reducing the thundering herd effect.

### Full Jitter

Full jitter adds a random value between 0 and the calculated backoff interval:

```java
wait_interval = random(0, base * multiplier^n)
```

### Equal Jitter

Equal jitter adds a random value to half of the backoff interval:

```java
wait_interval = (base * multiplier^n) / 2 + random(0, (base * multiplier^n) / 2)
```

### Decorrelated Jitter

Decorrelated jitter uses a more sophisticated approach that decorrelates retry attempts:

```java
wait_interval = random(base, previous_wait_interval * 3)
```

## Resilience4j Implementation

Resilience4j supports jitter through its IntervalFunction. Here's how to add jitter:

```java
IntervalFunction intervalFn = IntervalFunction.ofExponentialRandomBackoff(
    INITIAL_INTERVAL, 
    MULTIPLIER, 
    RANDOMIZATION_FACTOR
);
```

Where `RANDOMIZATION_FACTOR` is a value between 0 and 1 that determines the amount of jitter.

## Complete Example

Here's a complete example demonstrating exponential backoff with jitter:

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

If you need more control, you can implement custom jitter:

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

## Best Practices

1. **Use exponential backoff** for transient failures
2. **Add jitter** to prevent thundering herd problems
3. **Set maximum retry limits** to prevent infinite retries
4. **Log retry attempts** for debugging and monitoring
5. **Consider circuit breakers** for persistent failures
6. **Use appropriate jitter strategy** based on your use case:
   - Full jitter: Better for high concurrency
   - Equal jitter: More predictable, less variance
   - Decorrelated jitter: Best for distributed systems

## Kotlin Implementation

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

### Example Usage

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

## Summary

In this tutorial, we've covered how to improve client retries using exponential backoff and jitter. Exponential backoff helps prevent overwhelming a failing service, while jitter prevents the thundering herd problem by randomizing retry timing.

The combination of exponential backoff and jitter provides a robust retry strategy for distributed systems, ensuring that retries are both respectful to the service and distributed over time to avoid synchronized retry storms.
