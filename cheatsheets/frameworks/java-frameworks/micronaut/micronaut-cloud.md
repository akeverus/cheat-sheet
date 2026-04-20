---
title: "Micronaut: Cloud Native - Service Discovery, Configuration и Distributed Tracing"
description: "Полное руководство по cloud-native возможностям Micronaut: service discovery, distributed configuration, circuit breaker, tracing"
tags:
  - micronaut
  - cloud
  - kubernetes
  - service-discovery
  - circuit-breaker
  - tracing
  - java
  - kotlin
difficulty: "intermediate"
prerequisites: ["micronaut/micronaut-basics.md", "micronaut/micronaut-http.md"]
next: ["micronaut-graalvm.md"]
updated: "2026-02-11"
related: ["micronaut-reactive.md", "micronaut-security.md"]
---

# Micronaut: Cloud Native - Service Discovery, Configuration и Distributed Tracing

## Полезные ссылки

[Официальная документация Micronaut](https://docs.micronaut.io/)
[Micronaut GitHub](https://github.com/micronaut-projects/micronaut-core)

## Содержание

- [Micronaut: Cloud Native — Service Discovery, Configuration и Distributed Tracing](#micronaut-cloud-native-service-discovery-configuration-и-distributed-tracing)
- [Введение](#введение)
  - [Основные возможности](#основные-возможности)
- [Service Discovery](#service-discovery)
  - [Consul](#consul)
  - [Eureka](#eureka)
  - [Kubernetes Service Discovery](#kubernetes-service-discovery)
- [Distributed Configuration](#distributed-configuration)
  - [Consul Configuration](#consul-configuration)
  - [Vault Configuration](#vault-configuration)
- [Circuit Breaker](#circuit-breaker)
  - [Настройка](#настройка)
  - [Использование Circuit Breaker](#использование-circuit-breaker)
  - [Retry](#retry)
- [Distributed Tracing](#distributed-tracing)
  - [Zipkin](#zipkin)
  - [Jaeger](#jaeger)
  - [Использование Tracing](#использование-tracing)
- [Health Checks](#health-checks)
  - [Custom Health Indicators](#custom-health-indicators)
- [Metrics](#metrics)
  - [Micrometer Integration](#micrometer-integration)
  - [Custom Metrics](#custom-metrics)
- [Kubernetes](#kubernetes)
  - [Deployment Configuration](#deployment-configuration)
  - [Service Configuration](#service-configuration)
- [Лучшие практики](#лучшие-практики)
  - [1. Используйте Service Discovery](#1-используйте-service-discovery)
- [ Хорошо](#хорошо)
  - [2. Настройте Circuit Breaker](#2-настройте-circuit-breaker)
  - [3. Используйте Distributed Tracing](#3-используйте-distributed-tracing)
  - [4. Настройте Health Checks](#4-настройте-health-checks)
  - [5. Используйте Metrics](#5-используйте-metrics)
- [Rate Limiting](#rate-limiting)
  - [Настройка Rate Limiting](#настройка-rate-limiting)
  - [Custom Rate Limiter](#custom-rate-limiter)
- [Load Balancing](#load-balancing)
  - [Client-side Load Balancing](#client-side-load-balancing)
  - [Load Balancer Configuration](#load-balancer-configuration)
- [Service Mesh Integration](#service-mesh-integration)
  - [Istio Integration](#istio-integration)
  - [Configuration Refresh](#configuration-refresh)
  - [Configuration Properties](#configuration-properties)
- [Service Mesh](#service-mesh)
  - [Istio Sidecar](#istio-sidecar)
  - [Envoy Configuration](#envoy-configuration)
- [Configuration Management](#configuration-management)
- [Kubernetes Integration](#kubernetes-integration)
  - [Kubernetes Deployment](#kubernetes-deployment)
  - [Kubernetes Service](#kubernetes-service)
  - [Tracing Configuration](#tracing-configuration)
  - [Custom Spans](#custom-spans)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение

**Micronaut** оптимизирован для **cloud-native** приложений с поддержкой **service discovery**, **distributed configuration**, **circuit breakers**, **distributed tracing** и других паттернов микросервисной архитектуры.

### Основные возможности

- **Service Discovery**: Интеграция с **Consul**, **Eureka**, **Kubernetes**
- **Distributed Configuration**: Конфигурация из внешних источников
- **Circuit Breaker**: Защита от каскадных сбоев
- **Distributed Tracing**: Трассировка запросов через микросервисы
- **Health Checks**: **Health endpoints** для мониторинга
- **Metrics**: Интеграция с **Micrometer** и **Prometheus**
- **Kubernetes**: Нативная поддержка **Kubernetes**

## Service Discovery

### Consul

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.discovery:micronaut-discovery-client")
    implementation("io.micronaut.consul:micronaut-consul-discovery")
}
```

**application.yml:**

```yaml
micronaut:
  application:
    name: my-service
  discovery:
    consul:
      enabled: true
      defaultZone: "consul:8500"
      registration:
        enabled: true
        health-path: /health
        health-interval: 10s
```

### Eureka

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.discovery:micronaut-discovery-client")
    implementation("io.micronaut.eureka:micronaut-eureka-client")
}
```

**application.yml:**

```yaml
micronaut:
  application:
    name: my-service
  discovery:
    client:
      enabled: true
    eureka:
      client:
        defaultZone: "http://localhost:8761/eureka"
      instance:
        prefer-ip-address: true
```

### Kubernetes Service Discovery

**application.yml:**

```yaml
micronaut:
  application:
    name: my-service
  discovery:
    kubernetes:
      enabled: true
      namespace: default
```

## Distributed Configuration

### Consul Configuration

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.consul:micronaut-consul")
}
```

**application.yml:**

```yaml
micronaut:
  config:
    consul:
      enabled: true
      defaultZone: "consul:8500"
      paths:
        - config/my-service
```

### Vault Configuration

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.vault:micronaut-vault")
}
```

**application.yml:**

```yaml
micronaut:
  config:
    vault:
      enabled: true
      uri: http://vault:8200
      token: ${VAULT_TOKEN}
      kv:
        enabled: true
        path: secret/my-service
```

## Circuit Breaker

### Настройка

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.micronaut-runtime")
    implementation("io.micronaut.resilience:micronaut-resilience4j")
}
```

**application.yml:**

```yaml
resilience4j:
  circuitbreaker:
    instances:
      external-api:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
        waitDurationInOpenState: 10s
        failureRateThreshold: 50
        slowCallRateThreshold: 100
        slowCallDurationThreshold: 2s
```

### Использование Circuit Breaker

```java
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.inject.Singleton;

@Singleton
public class ExternalApiService {

    @CircuitBreaker(name = "external-api", fallbackMethod = "fallback")
    public String callExternalApi(String data) {
        // Вызов внешнего API
        return externalApiClient.call(data);
    }

    public String fallback(String data, Exception e) {
        return "Fallback response for: " + data;
    }
}
```

### Retry

**application.yml:**

```yaml
resilience4j:
  retry:
    instances:
      external-api:
        maxAttempts: 3
        waitDuration: 1s
        retryExceptions:
          - java.net.SocketTimeoutException
          - java.io.IOException
```

```java
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.inject.Singleton;

@Singleton
public class ExternalApiService {

    @Retry(name = "external-api")
    public String callExternalApi(String data) {
        return externalApiClient.call(data);
    }
}
```

## Distributed Tracing

### Zipkin

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.tracing:micronaut-tracing-zipkin")
}
```

**application.yml:**

```yaml
tracing:
  zipkin:
    enabled: true
    http:
      url: http://zipkin:9411
    sampler:
      probability: 1.0
```

### Jaeger

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.tracing:micronaut-tracing-jaeger")
}
```

**application.yml:**

```yaml
tracing:
  jaeger:
    enabled: true
    sampler:
      probability: 1.0
```

### Использование Tracing

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.opentracing.Tracer;
import io.opentracing.Span;
import jakarta.inject.Inject;

@Controller("/api")
public class ApiController {

    @Inject
    Tracer tracer;

    @Get("/users/{id}")
    public User getUser(Long id) {
        Span span = tracer.buildSpan("get-user").start();
        try {
            return userService.findById(id);
        } finally {
            span.finish();
        }
    }
}
```

## Health Checks

### Настройка

**application.yml:**

```yaml
micronaut:
  endpoints:
    health:
      enabled: true
      sensitive: false
      details-visible: ANONYMOUS
```

### Custom Health Indicators

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

## Metrics

### Micrometer Integration

**build.gradle:**

```gradle
dependencies {
    implementation("io.micronaut.micrometer:micronaut-micrometer-core")
    implementation("io.micronaut.micrometer:micronaut-micrometer-registry-prometheus")
}
```

**application.yml:**

```yaml
micronaut:
  metrics:
    enabled: true
    export:
      prometheus:
        enabled: true
        descriptions: true
        step: PT1M
```

### Custom Metrics

```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.inject.Singleton;

@Singleton
public class UserService {
    private final Counter userCreatedCounter;
    private final Timer userCreationTimer;

    public UserService(MeterRegistry meterRegistry) {
        this.userCreatedCounter = Counter.builder("users.created")
            .description("Number of users created")
            .register(meterRegistry);
        this.userCreationTimer = Timer.builder("users.creation.time")
            .description("Time taken to create a user")
            .register(meterRegistry);
    }

    public User createUser(User user) {
        return userCreationTimer.recordCallable(() -> {
            User created = userRepository.save(user);
            userCreatedCounter.increment();
            return created;
        });
    }
}
```

## Kubernetes

### Deployment Configuration

**deployment.yaml:**

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-service
  template:
    metadata:
      labels:
        app: my-service
    spec:
      containers:
      - name: my-service
        image: my-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: MICRONAUT_ENVIRONMENTS
          value: "k8s"
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /health
            port: 8080
          initialDelaySeconds: 10
          periodSeconds: 5
```

### Service Configuration

**service.yaml:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: my-service
  ports:
  - port: 80
    targetPort: 8080
  type: ClusterIP
```

## Лучшие практики

### 1. Используйте Service Discovery

```yaml
# ✅ Хорошо
micronaut:
  discovery:
    consul:
      enabled: true
```

### 2. Настройте Circuit Breaker

```java
// ✅ Хорошо
@CircuitBreaker(name = "external-api", fallbackMethod = "fallback")
public String callExternalApi(String data) {
    // ...
}
```

### 3. Используйте Distributed Tracing

```yaml
# ✅ Хорошо
tracing:
  zipkin:
    enabled: true
```

### 4. Настройте Health Checks

```yaml
# ✅ Хорошо
micronaut:
  endpoints:
    health:
      enabled: true
```

### 5. Используйте Metrics

```java
// ✅ Хорошо
Counter.builder("users.created")
    .register(meterRegistry);
```

## Rate Limiting

### Настройка Rate Limiting

**application.yml:**

```yaml
micronaut:
  http:
    server:
      rate-limit:
        enabled: true
        default-limit: 100
        default-period: PT1M
```

### Custom Rate Limiter

```java
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.ratelimit.annotation.RateLimited;

@Controller("/api")
public class RateLimitedController {

    @Get("/users")
    @RateLimited(limit = 10, duration = "PT1M")
    public List<User> getUsers() {
        return userService.findAll();
    }

    @Get("/users/{id}")
    @RateLimited(limit = 100, duration = "PT1M", key = "#{id}")
    public User getUser(Long id) {
        return userService.findById(id);
    }
}
```

## Load Balancing

### Client-side Load Balancing

```java
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.discovery.annotation.ServiceId;

@Client(id = "user-service")
public interface UserServiceClient {

    @Get("/users/{id}")
    User getUser(Long id);
}
```

### Load Balancer Configuration

**application.yml:**

```yaml
micronaut:
  http:
    client:
      load-balancer:
        selection-strategy: round-robin
```

## Service Mesh Integration

### Istio Integration

```yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: my-service
spec:
  hosts:
  - my-service
  http:
  - match:
    - uri:
        prefix: /api
    route:
    - destination:
        host: my-service
        subset: v1
      weight: 80
    - destination:
        host: my-service
        subset: v2
      weight: 20
```

## Distributed Configuration

### Configuration Refresh

```java
import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.configuration.refresh.RefreshEvent;
import jakarta.inject.Singleton;

@Singleton
public class ConfigurableService {

    @Value("${app.feature.enabled:false}")
    private boolean featureEnabled;

    @EventListener
    public void onRefresh(RefreshEvent event) {
        // Обработка обновления конфигурации
        log.info("Configuration refreshed, feature enabled: {}", featureEnabled);
    }
}
```

### Configuration Properties

```java
import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@ConfigurationProperties("app.service")
public class ServiceConfiguration {

    @NotBlank
    private String name;

    @Min(1)
    private int maxRetries = 3;

    private Duration timeout = Duration.ofSeconds(5);

    // Getters and setters
}
```

## Service Mesh

### Istio Sidecar

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-service
  labels:
    app: my-service
spec:
  containers:
  - name: my-service
    image: my-service:latest
  - name: istio-proxy
    image: istio/proxyv2:latest
```

### Envoy Configuration

```yaml
static_resources:
  listeners:
  - name: listener_0
    address:
      socket_address:
        address: 0.0.0.0
        port_value: 8080
    filter_chains:
    - filters:
      - name: envoy.filters.network.http_connection_manager
        typed_config:
          "@type": type.googleapis.com/envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager
          stat_prefix: ingress_http
          route_config:
            name: local_route
            virtual_hosts:
            - name: local_service
              domains: ["*"]
              routes:
              - match:
                  prefix: "/"
                route:
                  cluster: service_cluster
```

## Configuration Management

### Configuration Refresh

```java
import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.configuration.refresh.RefreshEvent;
import jakarta.inject.Singleton;

@Singleton
public class ConfigurableService {

    @Value("${app.feature.enabled:false}")
    private boolean featureEnabled;

    @EventListener
    public void onRefresh(RefreshEvent event) {
        // Обработка обновления конфигурации
        log.info("Configuration refreshed, feature enabled: {}", featureEnabled);
    }
}
```

### Configuration Properties

```java
import io.micronaut.context.annotation.ConfigurationProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

@ConfigurationProperties("app.service")
public class ServiceConfiguration {

    @NotBlank
    private String name;

    @Min(1)
    private int maxRetries = 3;

    private Duration timeout = Duration.ofSeconds(5);

    // Getters and setters
}
```

## Service Mesh

### Istio Sidecar

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: my-service
  labels:
    app: my-service
spec:
  containers:
  - name: my-service
    image: my-service:latest
  - name: istio-proxy
    image: istio/proxyv2:latest
```

### Envoy Configuration

```yaml
static_resources:
  listeners:
  - name: listener_0
    address:
      socket_address:
        address: 0.0.0.0
        port_value: 8080
    filter_chains:
    - filters:
      - name: envoy.filters.network.http_connection_manager
        typed_config:
          "@type": type.googleapis.com/envoy.extensions.filters.network.http_connection_manager.v3.HttpConnectionManager
          stat_prefix: ingress_http
          route_config:
            name: local_route
            virtual_hosts:
            - name: local_service
              domains: ["*"]
              routes:
              - match:
                  prefix: "/"
                route:
                  cluster: service_cluster
```

## Kubernetes Integration

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: micronaut-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: micronaut-app
  template:
    metadata:
      labels:
        app: micronaut-app
    spec:
      containers:
      - name: micronaut-app
        image: micronaut-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: KUBERNETES_NAMESPACE
          valueFrom:
            fieldRef:
              fieldPath: metadata.namespace
```

### Kubernetes Service

```yaml
apiVersion: v1
kind: Service
metadata:
  name: micronaut-app
spec:
  selector:
    app: micronaut-app
  ports:
  - port: 80
    targetPort: 8080
  type: LoadBalancer
```

## Distributed Tracing

### Tracing Configuration

**application.yml:**

```yaml
tracing:
  zipkin:
    enabled: true
    url: http://zipkin:9411
  jaeger:
    enabled: false
    url: http://jaeger:14268/api/traces
```

### Custom Spans

```java
import io.micronaut.tracing.annotation.NewSpan;
import io.micronaut.tracing.annotation.SpanTag;
import jakarta.inject.Singleton;

@Singleton
public class TracedService {

    @NewSpan("user-operation")
    public User getUser(@SpanTag("user.id") Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```


## Заключение

**Micronaut** предоставляет полный набор инструментов для создания **cloud-native** приложений с поддержкой **service discovery**, **distributed configuration**, **circuit breakers**, **distributed tracing**, **rate limiting**, **load balancing**, **service mesh integration**, **configuration refresh**, **configuration management**, **Kubernetes integration**, **custom spans** и других паттернов микросервисной архитектуры.

## Дополнительные ресурсы

- [**Micronaut Cloud** Documentation](https://micronaut-projects.github.io/micronaut-kubernetes/latest/guide/)
- [**Micronaut Service Discovery**](https://micronaut-projects.github.io/micronaut-discovery-client/latest/guide/)
- [**Micronaut** Tracing](https://micronaut-projects.github.io/micronaut-tracing/latest/guide/)
- [**Kubernetes** Documentation](https://kubernetes.io/docs/)
- [Istio Documentation](https://istio.io/latest/docs/)
- [Envoy Documentation](https://www.envoyproxy.io/docs)
- [Zipkin Documentation](https://zipkin.io/pages/instrumenting.html)
- [Jaeger Documentation](https://www.jaegertracing.io/docs/)

## См. также

- [[micronaut-actuator|Micronaut: Actuator — Health Checks, Metrics и Endpoints]]
- [[micronaut-basics|Micronaut: Основы]]
- [[micronaut-batch|Micronaut: Batch Processing — Job Processing и Scheduling]]
- [[micronaut-cache|Micronaut: Caching — Cache Abstraction и Redis Cache]]
- [[micronaut-core|Micronaut: Core — Dependency Injection и Bean Management]]
