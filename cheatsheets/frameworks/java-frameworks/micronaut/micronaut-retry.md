---
title: "Micronaut: Retry — Retry Logic и Circuit Breaker"
description: "Полное руководство по retry logic в Micronaut: retry annotations, circuit breakers, exponential backoff и best practices"
tags:
  - micronaut
  - retry
  - circuit-breaker
  - resilience
  - backoff
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-core.md"]
next: ["micronaut-core.md", "micronaut-cloud.md"]
updated: "2026-04-20"
related: ["micronaut-core.md", "micronaut-cloud.md"]
---

# Micronaut: Retry — Retry Logic и Circuit Breaker

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Retry](#настройка-retry)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Basic Retry](#basic-retry)
  - [@Retryable Annotation](#retryable-annotation)
  - [Retry with Exponential Backoff](#retry-with-exponential-backoff)
- [Circuit Breaker](#circuit-breaker)
  - [Circuit Breaker Pattern](#circuit-breaker-pattern)
- [Fallback Methods](#fallback-methods)
  - [Fallback Implementation](#fallback-implementation)
- [Custom Retry Policies](#custom-retry-policies)
  - [Custom Retry Policy](#custom-retry-policy)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте retry для transient errors](#1-используйте-retry-для-transient-errors)
  - [2. Используйте circuit breaker для защиты](#2-используйте-circuit-breaker-для-защиты)
  - [3. Всегда предоставляйте fallback](#3-всегда-предоставляйте-fallback)
- [Retry Configuration](#retry-configuration)
  - [Global Retry Configuration](#global-retry-configuration)
  - [Service-specific Retry](#service-specific-retry)
- [Retry Metrics](#retry-metrics)
  - [Retry Monitoring](#retry-monitoring)
- [Retry with Predicate](#retry-with-predicate)
  - [Conditional Retry](#conditional-retry)
- [Circuit Breaker States](#circuit-breaker-states)
  - [State Monitoring](#state-monitoring)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut** предоставляет мощную поддержку **retry logic** и **circuit breakers** для создания устойчивых приложений. Это позволяет автоматически повторять неудачные операции и защищать систему от каскадных сбоев.

### Основные возможности

- **@Retryable**: Аннотация для автоматического **retry**
- **Circuit Breaker**: Защита от каскадных сбоев
- **Exponential Backoff**: Экспоненциальная задержка между попытками
- **Custom Retry Policies**: пользовательские политики **retry**
- **Fallback Methods**: Методы **fallback** при ошибках

## Настройка Retry

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.retry:micronaut-retry")
}
```

### Конфигурация

**application.yml:**

```yaml
micronaut:
  retry:
    enabled: true
```

## Basic Retry

### @Retryable Annotation

```java
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Singleton;

@Singleton
public class RetryableService {

    @Retryable(attempts = "3", delay = "1s")
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

### Retry with Exponential Backoff

```java
@Singleton
public class ExponentialBackoffService {

    @Retryable(
        attempts = "5",
        delay = "1s",
        multiplier = "2",
        maxDelay = "10s"
    )
    public void processData() {
        // Операция с exponential backoff
        externalService.call();
    }
}
```

## Circuit Breaker

### Circuit Breaker Pattern

```java
import io.micronaut.retry.annotation.CircuitBreaker;
import jakarta.inject.Singleton;

@Singleton
public class CircuitBreakerService {

    @CircuitBreaker(
        attempts = "3",
        delay = "1s",
        reset = "30s"
    )
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

## Fallback Methods

### Fallback Implementation

```java
import io.micronaut.retry.annotation.Fallback;
import jakarta.inject.Singleton;

@Singleton
public class FallbackService {

    @Retryable(attempts = "3")
    @Fallback(fallbackMethod = "getUserFallback")
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User getUserFallback(Long id) {
        // Fallback логика
        return new User("Default", "default@example.com");
    }
}
```

## Custom Retry Policies

### Custom Retry Policy

```java
import io.micronaut.retry.RetryPolicy;
import io.micronaut.retry.RetryState;
import jakarta.inject.Singleton;

@Singleton
public class CustomRetryPolicy implements RetryPolicy {

    @Override
    public boolean canRetry(RetryState retryState) {
        return retryState.getAttemptCount() < 5 &&
               !(retryState.getLastError() instanceof IllegalArgumentException);
    }

    @Override
    public Duration getDelay(RetryState retryState) {
        return Duration.ofSeconds((long) Math.pow(2, retryState.getAttemptCount()));
    }
}
```

## Лучшие практики

### 1. Используйте retry для transient errors

```java
// ✅ Хорошо
@Retryable(attempts = "3")
public void callExternalService() {
    // Retry для временных ошибок
}
```

### 2. Используйте circuit breaker для защиты

```java
// ✅ Хорошо
@CircuitBreaker(attempts = "3", reset = "30s")
public void callUnreliableService() {
    // Circuit breaker защищает от каскадных сбоев
}
```

### 3. Всегда предоставляйте fallback

```java
// ✅ Хорошо
@Retryable
@Fallback(fallbackMethod = "fallback")
public User getUser(Long id) {
    // ...
}
```

## Retry Configuration

### Global Retry Configuration

**application.yml:**

```yaml
micronaut:
  retry:
    enabled: true
    default:
      attempts: 3
      delay: 1s
      multiplier: 2
      max-delay: 10s
```

### Service-specific Retry

```java
@Singleton
@Retryable(
    attempts = "5",
    delay = "2s",
    multiplier = "1.5",
    maxDelay = "30s"
)
public class ConfiguredRetryService {

    public void processData() {
        // Операция с настроенным retry
        externalService.call();
    }
}
```

## Retry Metrics

### Retry Monitoring

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Singleton;

@Singleton
public class RetryMetricsService {
    private final Counter retryCounter;
    private final Counter failureCounter;

    public RetryMetricsService(MeterRegistry meterRegistry) {
        this.retryCounter = Counter.builder("retry.attempts")
            .description("Number of retry attempts")
            .register(meterRegistry);
        this.failureCounter = Counter.builder("retry.failures")
            .description("Number of retry failures")
            .register(meterRegistry);
    }

    public void recordRetry() {
        retryCounter.increment();
    }

    public void recordFailure() {
        failureCounter.increment();
    }
}
```

## Retry with Predicate

### Conditional Retry

```java
import io.micronaut.retry.annotation.Retryable;
import jakarta.inject.Singleton;

@Singleton
public class ConditionalRetryService {

    @Retryable(
        attempts = "3",
        delay = "1s",
        predicate = RetryablePredicate.class
    )
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}

public class RetryablePredicate implements Predicate<Throwable> {

    @Override
    public boolean test(Throwable throwable) {
        // Retry только для определенных исключений
        return throwable instanceof SQLException ||
               throwable instanceof TimeoutException;
    }
}
```

## Circuit Breaker States

### State Monitoring

```java
import io.micronaut.retry.CircuitBreaker;
import jakarta.inject.Singleton;

@Singleton
public class CircuitBreakerStateService {
    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerStateService(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    public CircuitBreaker.State getState() {
        return circuitBreaker.getState();
    }

    public void reset() {
        circuitBreaker.reset();
    }
}
```


## Заключение

**Micronaut Retry** предоставляет мощные инструменты для создания устойчивых приложений. Поддержка **retry logic**, **circuit breakers**, **exponential backoff**, **fallback methods**, **custom retry policies**, **retry configuration**, **metrics**, **conditional retry**, **circuit breaker states** и других продвинутых возможностей позволяет создавать надежные приложения, которые могут восстанавливаться после временных сбоев.

## Дополнительные ресурсы

- [**Micronaut Retry** Documentation](https://micronaut-projects.github.io/micronaut-retry/latest/guide/)
- [**Circuit Breaker** Pattern](https://martinfowler.com/bliki/CircuitBreaker.html)
- [**Resilience4j** Documentation](https://resilience4j.readme.io/)
- [Retry Patterns](https://docs.aws.amazon.com/general/latest/gr/api-retries.html)

## См. также

- [Micronaut: Actuator — Health Checks, Metrics и Endpoints](micronaut-actuator.md)
- [Micronaut: Основы](micronaut-basics.md)
- [Micronaut: Batch Processing — Job Processing и Scheduling](micronaut-batch.md)
- [Micronaut: Caching — Cache Abstraction и Redis Cache](micronaut-cache.md)
- [Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing](micronaut-cloud.md)
