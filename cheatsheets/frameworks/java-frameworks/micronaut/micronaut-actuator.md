---
title: "Micronaut: Actuator - Health Checks, Metrics и Endpoints"
description: "Полное руководство по Micronaut Actuator: health checks, metrics, endpoints, monitoring и best practices"
tags:
  - micronaut
  - actuator
  - health
  - metrics
  - monitoring
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-core.md"]
next: ["micronaut-cloud.md", "micronaut-testing.md"]
updated: "2026-04-20"
related: ["micronaut-core.md", "micronaut-cloud.md"]
---

# Micronaut: Actuator - Health Checks, Metrics и Endpoints

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Настройка Actuator](#настройка-actuator)
  - [Зависимости](#зависимости)
  - [Конфигурация](#конфигурация)
- [Health Checks](#health-checks)
  - [Basic Health Check](#basic-health-check)
  - [Custom Health Indicator](#custom-health-indicator)
- [Metrics](#metrics)
  - [Custom Metrics](#custom-metrics)
- [Custom Endpoints](#custom-endpoints)
  - [Custom Endpoint](#custom-endpoint)
- [Лучшие практики](#лучшие-практики)
  - [1. Настраивайте security для endpoints](#1-настраивайте-security-для-endpoints)
  - [2. Используйте health indicators](#2-используйте-health-indicators)
  - [3. Собирайте метрики для важных операций](#3-собирайте-метрики-для-важных-операций)
- [Info Endpoint](#info-endpoint)
  - [Application Info](#application-info)
  - [Custom Info](#custom-info)
- [Prometheus Integration](#prometheus-integration)
  - [Prometheus Metrics](#prometheus-metrics)
  - [Custom Prometheus Metrics](#custom-prometheus-metrics)
- [Loggers Endpoint](#loggers-endpoint)
  - [Dynamic Logging Configuration](#dynamic-logging-configuration)
- [Thread Dump Endpoint](#thread-dump-endpoint)
  - [Thread Information](#thread-information)
- [Environment Endpoint](#environment-endpoint)
  - [Environment Information](#environment-information)
- [Beans Endpoint](#beans-endpoint)
  - [Bean Information](#bean-information)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Micronaut Management** предоставляет **endpoints** для мониторинга и управления приложением, аналогично **Spring Boot Actuator**. Это позволяет отслеживать здоровье приложения, метрики и другую информацию.

### Основные возможности

- **Health Checks**: Проверка здоровья приложения
- **Metrics**: Сбор метрик
- **Info Endpoint**: Информация о приложении
- **Custom Endpoints**: Создание собственных **endpoints**
- **Prometheus Integration**: Интеграция с **Prometheus**

## Настройка Actuator

### Зависимости

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.micrometer:micronaut-micrometer-registry-prometheus")
}
```

### Конфигурация

**application.yml:**

```yaml
# Включение endpoints Actuator (health, metrics и т.д.)
micronaut:
  endpoints:
    all:
      enabled: true
      sensitive: false
    health:
      enabled: true
      sensitive: false
    metrics:
      enabled: true
      sensitive: false
```

## Health Checks

### Basic Health Check

```java
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton
public class DatabaseHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Publisher<HealthResult> getResult() {
        return Mono.fromCallable(() -> {
            try (Connection connection = dataSource.getConnection()) {
                boolean valid = connection.isValid(1);
                return HealthResult.builder("database")
                    .status(valid ? HealthStatus.UP : HealthStatus.DOWN)
                    .build();
            } catch (Exception e) {
                return HealthResult.builder("database")
                    .status(HealthStatus.DOWN)
                    .exception(e)
                    .build();
            }
        });
    }
}
```

### Custom Health Indicator

```java
// Реализация своего индикатора здоровья приложения
@Singleton
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Publisher<HealthResult> getResult() {
        return Mono.just(HealthResult.builder("custom")
            .status(HealthStatus.UP)
            .build());
    }
}
```

## Metrics

### Custom Metrics

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.inject.Singleton;

@Singleton
public class MetricsService {
    private final Counter userCreatedCounter;
    private final Timer userCreationTimer;

    public MetricsService(MeterRegistry meterRegistry) {
        this.userCreatedCounter = Counter.builder("users.created")
            .description("Number of users created")
            .register(meterRegistry);
        this.userCreationTimer = Timer.builder("users.creation.time")
            .description("Time taken to create a user")
            .register(meterRegistry);
    }

    public void recordUserCreated() {
        userCreatedCounter.increment();
    }

    public void recordUserCreationTime(Duration duration) {
        userCreationTimer.record(duration);
    }
}
```

## Custom Endpoints

### Custom Endpoint

```java
// Кастомный endpoint Actuator с аннотацией @Endpoint и @Read
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;
import jakarta.inject.Singleton;

@Endpoint(id = "custom", defaultEnabled = true)
@Singleton
public class CustomEndpoint {

    @Read
    public Map<String, Object> getInfo() {
        return Map.of(
            "status", "ok",
            "timestamp", LocalDateTime.now()
        );
    }
}
```

## Лучшие практики

### 1. Настраивайте security для endpoints

```yaml
# ✅ Хорошо
micronaut:
  endpoints:
    all:
      sensitive: true
```

### 2. Используйте health indicators

```java
// ✅ Хорошо
@Singleton
public class DatabaseHealthIndicator implements HealthIndicator {
    // ...
}
```

### 3. Собирайте метрики для важных операций

```java
// ✅ Хорошо
userCreatedCounter.increment();
```

## Info Endpoint

### Application Info

**application.yml:**

```yaml
info:
  app:
    name: my-app
    version: 1.0.0
    description: My Micronaut Application
```

### Custom Info

```java
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;
import jakarta.inject.Singleton;

@Endpoint(id = "info", defaultEnabled = true)
@Singleton
public class InfoEndpoint {

    @Read
    public Map<String, Object> getInfo() {
        return Map.of(
            "app", Map.of(
                "name", "my-app",
                "version", "1.0.0"
            ),
            "build", Map.of(
                "time", System.getProperty("build.time"),
                "git", Map.of(
                    "commit", System.getProperty("git.commit.id")
                )
            )
        );
    }
}
```

## Prometheus Integration

### Prometheus Metrics

**application.yml:**

```yaml
micronaut:
  metrics:
    export:
      prometheus:
        enabled: true
        descriptions: true
        step: PT1M
```

### Custom Prometheus Metrics

```java
import io.micrometer.prometheus.PrometheusMeterRegistry;
import jakarta.inject.Singleton;

@Singleton
public class PrometheusMetricsService {
    private final PrometheusMeterRegistry prometheusRegistry;

    public PrometheusMetricsService(PrometheusMeterRegistry prometheusRegistry) {
        this.prometheusRegistry = prometheusRegistry;
    }

    public void recordCustomMetric(String name, double value) {
        prometheusRegistry.gauge(name, value);
    }
}
```

## Loggers Endpoint

### Dynamic Logging Configuration

```java
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Write;
import jakarta.inject.Singleton;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

@Endpoint(id = "loggers", defaultEnabled = true)
@Singleton
public class LoggersEndpoint {

    @Write
    public void setLoggerLevel(String name, String level) {
        Logger logger = (Logger) LoggerFactory.getLogger(name);
        logger.setLevel(Level.valueOf(level));
    }
}
```

## Thread Dump Endpoint

### Thread Information

```java
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;
import jakarta.inject.Singleton;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@Endpoint(id = "threaddump", defaultEnabled = true)
@Singleton
public class ThreadDumpEndpoint {

    @Read
    public Map<String, Object> getThreadDump() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        return Map.of(
            "threads", threadMXBean.getThreadCount(),
            "daemonThreads", threadMXBean.getDaemonThreadCount(),
            "peakThreads", threadMXBean.getPeakThreadCount()
        );
    }
}
```

## Environment Endpoint

### Environment Information

```java
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;
import jakarta.inject.Singleton;
import java.util.Map;

@Endpoint(id = "env", defaultEnabled = true)
@Singleton
public class EnvironmentEndpoint {

    @Read
    public Map<String, Object> getEnvironment() {
        return Map.of(
            "activeProfiles", environment.getActiveProfiles(),
            "properties", environment.getPropertySources()
        );
    }
}
```

## Beans Endpoint

### Bean Information

```java
import io.micronaut.management.endpoint.annotation.Endpoint;
import io.micronaut.management.endpoint.annotation.Read;
import io.micronaut.context.ApplicationContext;
import jakarta.inject.Singleton;

@Endpoint(id = "beans", defaultEnabled = true)
@Singleton
public class BeansEndpoint {
    private final ApplicationContext applicationContext;

    public BeansEndpoint(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Read
    public Map<String, Object> getBeans() {
        return applicationContext.getBeanDefinitions().stream()
            .collect(Collectors.toMap(
                bd -> bd.getName(),
                bd -> Map.of("type", bd.getBeanType().getName())
            ));
    }
}
```


## Заключение

**Micronaut Management** предоставляет мощные инструменты для мониторинга и управления приложением. Поддержка **health checks**, **metrics**, **custom endpoints**, **Prometheus integration**, **info endpoint**, **loggers endpoint**, **thread dump**, **environment endpoint**, **beans endpoint** и других продвинутых возможностей позволяет эффективно отслеживать состояние приложения.

## Дополнительные ресурсы

- [**Micronaut Management** Documentation](https://micronaut-projects.github.io/micronaut-micrometer/latest/guide/)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [**JMX** Documentation](https://docs.oracle.com/javase/tutorial/jmx/)

## См. также

- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-cloud|Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing]]
- [[micronaut-core|Micronaut: Core — Dependency Injection и Bean Management]]
