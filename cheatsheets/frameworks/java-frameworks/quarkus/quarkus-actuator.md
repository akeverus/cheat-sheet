---
title: "Quarkus: Actuator - Health Checks и Metrics"
description: "Полное руководство по Actuator в Quarkus: health checks, metrics, info endpoints, custom endpoints и best practices"
tags:
  - quarkus
  - actuator
  - health
  - metrics
  - monitoring
  - java
difficulty: "intermediate"
prerequisites: ["quarkus/quarkus-basics.md", "quarkus/quarkus-core.md"]
next: ["quarkus-core.md", "quarkus-cloud.md"]
updated: "2026-04-20"
related: ["quarkus-core.md", "quarkus-cloud.md"]
---

# Quarkus: Actuator - Health Checks и Metrics

## Полезные ссылки

[Официальная документация Quarkus](https://quarkus.io/guides/)
[Quarkus GitHub](https://github.com/quarkusio/quarkus)

## Содержание

- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Health Checks](#health-checks)
  - [Basic Health Check](#basic-health-check)
  - [Readiness Check](#readiness-check)
- [Metrics](#metrics)
  - [Micrometer Integration](#micrometer-integration)
  - [Custom Metrics](#custom-metrics)
- [Лучшие практики](#лучшие-практики)
  - [1. Всегда настраивайте health checks](#1-всегда-настраивайте-health-checks)
  - [2. Используйте metrics для мониторинга](#2-используйте-metrics-для-мониторинга)
  - [3. Создавайте кастомные health checks для критических компонентов](#3-создавайте-кастомные-health-checks-для-критических-компонентов)
- [Startup Health Check](#startup-health-check)
  - [Startup Probe](#startup-probe)
- [Advanced Metrics](#advanced-metrics)
  - [Timers](#timers)
  - [Gauges](#gauges)
- [Info Endpoint](#info-endpoint)
  - [Application Info](#application-info)
  - [Custom Info](#custom-info)
- [Advanced Health Check Patterns](#advanced-health-check-patterns)
  - [Composite Health Checks](#composite-health-checks)
  - [Async Health Checks](#async-health-checks)
- [Advanced Metrics Patterns](#advanced-metrics-patterns)
  - [Histograms](#histograms)
  - [Metrics with Tags](#metrics-with-tags)
- [Custom Endpoints](#custom-endpoints)
  - [Custom Health Endpoint](#custom-health-endpoint)
  - [Custom Metrics Endpoint](#custom-metrics-endpoint)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)
- [См. также](#см-также)

## Введение

**Quarkus** предоставляет мощные инструменты для мониторинга и управления приложением через **SmallRye Health** и **Micrometer**. Это позволяет отслеживать состояние приложения, метрики производительности и другую диагностическую информацию.

### Основные возможности

- **Health Checks**: Проверка состояния приложения
- **Metrics**: Метрики производительности
- **Info Endpoints**: Информация о приложении
- **Custom Endpoints**: Кастомные **endpoints**

## Health Checks

### Basic Health Check

**Базовый **health check**:**

```java
// Health check для Liveness (приложение живо)
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import jakarta.enterprise.context.ApplicationScoped;

@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Application")
            .up()
            .withData("status", "alive")
            .build();
    }
}
```

### Readiness Check

**Readiness check**:**

```java
import org.eclipse.microprofile.health.Readiness;
import jakarta.inject.Inject;

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {

    @Inject
    DataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        boolean isReady = checkDatabase();
        return HealthCheckResponse.named("Database")
            .status(isReady)
            .withData("database", isReady ? "connected" : "disconnected")
            .build();
    }

    private boolean checkDatabase() {
        try {
            dataSource.getConnection().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

## Metrics

### Micrometer Integration

**Интеграция с **Micrometer**:**

```properties
# application.properties
quarkus.micrometer.enabled=true
quarkus.micrometer.export.prometheus.enabled=true
quarkus.micrometer.export.prometheus.path=/metrics
```

### Custom Metrics

**Создание кастомных метрик:**

```java
// Счётчик метрик через Micrometer
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MetricsService {

    private final Counter requestCounter;
    private final Counter errorCounter;

    @Inject
    public MetricsService(MeterRegistry registry) {
        this.requestCounter = Counter.builder("requests.total")
            .description("Total number of requests")
            .register(registry);

        this.errorCounter = Counter.builder("errors.total")
            .description("Total number of errors")
            .register(registry);
    }

    public void incrementRequest() {
        requestCounter.increment();
    }

    public void incrementError() {
        errorCounter.increment();
    }
}
```

## Лучшие практики

### 1. Всегда настраивайте health checks

```java
// ✅ Хорошо
@Liveness
@ApplicationScoped
public class LivenessCheck implements HealthCheck {
    // ...
}
```

### 2. Используйте metrics для мониторинга

```properties
# ✅ Хорошо
quarkus.micrometer.enabled=true
quarkus.micrometer.export.prometheus.enabled=true
```

### 3. Создавайте кастомные health checks для критических компонентов

```java
// ✅ Хорошо
@Readiness
@ApplicationScoped
public class DatabaseReadinessCheck implements HealthCheck {
    // Проверка БД
}
```

## Startup Health Check

### Startup Probe

**Startup health check**:**

```java
import org.eclipse.microprofile.health.Startup;

@Startup
@ApplicationScoped
public class StartupCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Application Startup")
            .up()
            .withData("started", true)
            .build();
    }
}
```

## Advanced Metrics

### Timers

**Использование таймеров:**

```java
import io.micrometer.core.instrument.Timer;
import jakarta.inject.Inject;

@ApplicationScoped
public class TimedService {

    @Inject
    MeterRegistry registry;

    public void processRequest() {
        Timer.Sample sample = Timer.start(registry);
        try {
            // Обработка запроса
            process();
        } finally {
            sample.stop(Timer.builder("request.duration")
                .description("Request processing duration")
                .register(registry));
        }
    }
}
```

### Gauges

**Использование **gauges**:**

```java
@ApplicationScoped
public class GaugeService {

    @Inject
    MeterRegistry registry;

    @PostConstruct
    void init() {
        Gauge.builder("cache.size", this, GaugeService::getCacheSize)
            .description("Cache size")
            .register(registry);
    }

    private double getCacheSize() {
        return cache.size();
    }
}
```

## Info Endpoint

### Application Info

**Информация о приложении:**

```properties
# application.properties
quarkus.info.name=My Application
quarkus.info.version=1.0.0
quarkus.info.description=My Application Description
```

### Custom Info

**Кастомная информация:**

```java
import io.quarkus.info.BuildInfo;
import io.quarkus.info.GitInfo;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/info")
public class InfoResource {

    @Inject
    BuildInfo buildInfo;

    @Inject
    GitInfo gitInfo;

    @GET
    public Map<String, Object> getInfo() {
        return Map.of(
            "build", buildInfo,
            "git", gitInfo,
            "custom", "value"
        );
    }
}
```

## Advanced Health Check Patterns

### Composite Health Checks

**Составные **health checks**:**

```java
@Readiness
@ApplicationScoped
public class CompositeReadinessCheck implements HealthCheck {

    @Inject
    @Any
    Instance<HealthCheck> healthChecks;

    @Override
    public HealthCheckResponse call() {
        boolean allReady = healthChecks.stream()
            .allMatch(check -> check.call().getStatus() == Status.UP);

        return HealthCheckResponse.named("Composite")
            .status(allReady ? Status.UP : Status.DOWN)
            .build();
    }
}
```

### Async Health Checks

**Асинхронные **health checks**:**

```java
@Liveness
@ApplicationScoped
public class AsyncHealthCheck implements HealthCheck {

    @Override
    public Uni<HealthCheckResponse> call() {
        return checkExternalService()
            .map(isHealthy -> HealthCheckResponse.named("External Service")
                .status(isHealthy ? Status.UP : Status.DOWN)
                .build());
    }

    private Uni<Boolean> checkExternalService() {
        // Асинхронная проверка
        return Uni.createFrom().item(true);
    }
}
```

## Advanced Metrics Patterns

### Histograms

**Использование гистограмм:**

```java
@ApplicationScoped
public class HistogramService {

    @Inject
    MeterRegistry registry;

    public void recordValue(double value) {
        DistributionSummary.builder("request.size")
            .description("Request size distribution")
            .register(registry)
            .record(value);
    }
}
```

### Metrics with Tags

**Метрики с тегами:**

```java
@ApplicationScoped
public class TaggedMetricsService {

    @Inject
    MeterRegistry registry;

    public void recordRequest(String endpoint, String method, int statusCode) {
        Counter.builder("http.requests")
            .tag("endpoint", endpoint)
            .tag("method", method)
            .tag("status", String.valueOf(statusCode))
            .register(registry)
            .increment();
    }
}
```

## Custom Endpoints

### Custom Health Endpoint

**Кастомный **health endpoint**:**

```java
@Path("/health/custom")
public class CustomHealthEndpoint {

    @GET
    public Response customHealth() {
        boolean isHealthy = checkHealth();
        return Response.status(isHealthy ? 200 : 503)
            .entity(Map.of("status", isHealthy ? "UP" : "DOWN"))
            .build();
    }
}
```

### Custom Metrics Endpoint

**Кастомный **metrics endpoint**:**

```java
@Path("/metrics/custom")
public class CustomMetricsEndpoint {

    @Inject
    MeterRegistry registry;

    @GET
    public Map<String, Object> customMetrics() {
        return registry.getMeters().stream()
            .collect(Collectors.toMap(
                Meter::getId,
                this::getMeterValue
            ));
    }
}
```


## Заключение

**Quarkus Actuator** предоставляет мощные инструменты для мониторинга и управления приложением. Поддержка **health checks**, **metrics**, **info endpoints**, **custom endpoints** и других продвинутых возможностей позволяет отслеживать состояние и производительность приложения. Правильное использование **health checks**, метрик, таймеров, **gauges** и **info endpoints** являются ключевыми аспектами создания наблюдаемых приложений.

## Дополнительные ресурсы

- [**Quarkus Health** Guide](https://quarkus.io/guides/smallrye-health)
- [**Quarkus Metrics** Guide](https://quarkus.io/guides/smallrye-metrics)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Prometheus Documentation](https://prometheus.io/docs/)

icrometer.io/docs)
- [Prometheus Documentation](https://prometheus.io/docs/)

## См. также

- [[quarkus-basics|Quarkus: Основы]]
- [[quarkus-cache|Quarkus: Cache — Кеширование данных]]
- [[quarkus-cloud|Quarkus: Cloud Native — Kubernetes, OpenShift и Service Mesh]]
- [[quarkus-core|Quarkus: Core — CDI, Bean Scopes и Configuration]]
- [[quarkus-data|Quarkus: Data Access — Hibernate ORM, Panache и Repositories]]
