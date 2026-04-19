---
title: "Spring Actuator: Полное руководство по мониторингу и управлению"
description: "Комплексное руководство по Spring Actuator: endpoints, health checks, metrics, custom endpoints, security и best practices"
tags:
  - spring
  - actuator
  - monitoring
  - health
  - metrics
  - observability
  - java
difficulty: "intermediate"
prerequisites: ["spring/spring-boot.md", "spring/spring-security.md"]
next: ["spring/spring-cloud.md", "monitoring/prometheus.md"]
updated: "2026-02-11"
related: ["spring/spring-boot.md", "monitoring/prometheus.md"]
---

# Spring Actuator: Полное руководство по мониторингу и управлению



## Полезные ссылки

[Официальная документация Spring](https://docs.spring.io/)
[Spring Projects](https://spring.io/projects)

## Содержание

- [Введение в Spring Actuator](#введение-в-spring-actuator)
  - [Основные возможности](#основные-возможности)
  - [Архитектура Actuator](#архитектура-actuator)
- [Настройка Actuator](#настройка-actuator)
  - [Зависимости](#зависимости)
  - [Базовая конфигурация](#базовая-конфигурация)
- [Включение endpoints](#включение-endpoints)
- [Базовый путь](#базовый-путь)
- [Порт (по умолчанию тот же, что и приложение)](#порт-по-умолчанию-тот-же-что-и-приложение)
- [Health Endpoints](#health-endpoints)
  - [Базовое использование](#базовое-использование)
- [Включение health endpoint](#включение-health-endpoint)
  - [Проверка здоровья](#проверка-здоровья)
- [Простая проверка](#простая-проверка)
- [Детальная информация](#детальная-информация)
  - [Встроенные Health Indicators](#встроенные-health-indicators)
  - [Custom Health Indicators](#custom-health-indicators)
  - [Composite Health Indicators](#composite-health-indicators)
- [Metrics Endpoints](#metrics-endpoints)
- [Включение metrics endpoint](#включение-metrics-endpoint)
  - [Встроенные метрики](#встроенные-метрики)
  - [Просмотр метрик](#просмотр-метрик)
- [Все метрики](#все-метрики)
- [Конкретная метрика](#конкретная-метрика)
- [Метрика с тегами](#метрика-с-тегами)
  - [Custom Metrics](#custom-metrics)
  - [Gauge Metrics](#gauge-metrics)
- [Info Endpoint](#info-endpoint)
  - [Настройка Info](#настройка-info)
- [Включение info endpoint](#включение-info-endpoint)
- [Информация из application.properties](#информация-из-applicationproperties)
  - [Custom Info Contributor](#custom-info-contributor)
- [Custom Endpoints](#custom-endpoints)
  - [Создание Custom Endpoint](#создание-custom-endpoint)
  - [Web Endpoint](#web-endpoint)
- [Security](#security)
  - [Защита Endpoints](#защита-endpoints)
  - [Конфигурация через properties](#конфигурация-через-properties)
- [Базовый путь для security](#базовый-путь-для-security)
- [Включение security](#включение-security)
- [Prometheus Integration](#prometheus-integration)
  - [Настройка Prometheus](#настройка-prometheus)
- [Включение Prometheus endpoint](#включение-prometheus-endpoint)
  - [Использование](#использование)
- [Prometheus метрики](#prometheus-метрики)
- [Лучшие практики](#лучшие-практики)
  - [1. Ограничивайте доступ к endpoints](#1-ограничивайте-доступ-к-endpoints)
- [✅ Хорошо](#хорошо)
  - [2. Используйте security для production](#2-используйте-security-для-production)
  - [3. Создавайте custom health indicators](#3-создавайте-custom-health-indicators)
  - [4. Используйте метрики для мониторинга](#4-используйте-метрики-для-мониторинга)
  - [5. Настраивайте info endpoint](#5-настраивайте-info-endpoint)
- [Расширенные Health Indicators](#расширенные-health-indicators)
  - [Reactive Health Indicators](#reactive-health-indicators)
  - [Health Groups](#health-groups)
- [Группировка health indicators](#группировка-health-indicators)
  - [Health Status Aggregation](#health-status-aggregation)
- [Расширенные Metrics](#расширенные-metrics)
  - [Custom Meter Registry](#custom-meter-registry)
  - [Timed Annotations](#timed-annotations)
  - [Distribution Statistics](#distribution-statistics)
  - [Sliding Window Metrics](#sliding-window-metrics)
- [Интеграция с различными системами мониторинга](#интеграция-с-различными-системами-мониторинга)
  - [InfluxDB](#influxdb)
  - [Graphite](#graphite)
  - [CloudWatch](#cloudwatch)
  - [Datadog](#datadog)
- [Расширенные Endpoints](#расширенные-endpoints)
  - [JMX Endpoint](#jmx-endpoint)
  - [Conditional Endpoints](#conditional-endpoints)
  - [Endpoint Filters](#endpoint-filters)
- [Логирование и Tracing](#логирование-и-tracing)
  - [Loggers Endpoint](#loggers-endpoint)
- [Включение loggers endpoint](#включение-loggers-endpoint)
  - [HTTP Tracing](#http-tracing)
- [Включение HTTP tracing](#включение-http-tracing)
- [Управление приложением](#управление-приложением)
  - [Shutdown Endpoint](#shutdown-endpoint)
- [Включение shutdown endpoint](#включение-shutdown-endpoint)
  - [Environment Endpoint](#environment-endpoint)
- [Включение environment endpoint](#включение-environment-endpoint)
- [Производительность и оптимизация](#производительность-и-оптимизация)
  - [Метрики производительности](#метрики-производительности)
  - [Метрики памяти](#метрики-памяти)
- [Безопасность Actuator](#безопасность-actuator)
  - [Role-based Access Control](#role-based-access-control)
  - [IP-based Access Control](#ip-based-access-control)
- [Кастомизация Actuator](#кастомизация-actuator)
  - [Custom Endpoint Paths](#custom-endpoint-paths)
- [Кастомные пути для endpoints](#кастомные-пути-для-endpoints)
  - [Custom Endpoint Response](#custom-endpoint-response)
- [Интеграция с Kubernetes](#интеграция-с-kubernetes)
  - [Kubernetes Probes](#kubernetes-probes)
- [Настройка для Kubernetes](#настройка-для-kubernetes)
- [Заключение](#заключение)
- [Дополнительные ресурсы](#дополнительные-ресурсы)

## Введение в **Spring Actuator**

**Spring Actuator** предоставляет **production-ready** функции для мониторинга и управления **Spring Boot** приложениями. Он включает в себя множество встроенных **endpoints** для проверки здоровья приложения, метрик, информации о приложении и многого другого.

### Основные возможности

- **Health Checks**: Проверка состояния приложения и зависимостей
- **Metrics**: Сбор метрик производительности
- **Info**: Информация о приложении
- **Custom Endpoints**: Создание собственных **endpoints**
- **Security**: Защита **endpoints**
- **JMX и HTTP**: Доступ через **JMX** и **HTTP**

### Архитектура **Actuator**

```text
┌─────────────────────────────────────────────────────────┐
│              Spring Boot Application                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   Health     │  │   Metrics    │  │   Info       │  │
│  │   Indicators │  │   Registry   │  │   Endpoint   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Actuator Endpoints                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │   HTTP       │  │   JMX        │  │   Custom     │  │
│  │   Endpoints  │  │   Endpoints  │  │   Endpoints  │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## Настройка **Actuator**

### Зависимости

**Зависимость **spring-`boot-starter`-actuator** (**pom.xml**):**

```xml
<!-- Стартер Spring Actuator для мониторинга и управления -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Базовая конфигурация

```properties
# Включение endpoints
management.endpoints.web.exposure.include=health,info,metrics
management.endpoints.web.exposure.exclude=env,beans

# Базовый путь
management.endpoints.web.base-path=/actuator

# Порт (по умолчанию тот же, что и приложение)
management.server.port=8081
```

## Health Endpoints

### Базовое использование

```properties
# Включение health endpoint
management.endpoint.health.enabled=true
management.endpoint.health.show-details=when-authorized
```

### Проверка здоровья

```bash
# Простая проверка
curl http://localhost:8080/actuator/health

# Детальная информация
curl http://localhost:8080/actuator/health
```

### Встроенные **Health Indicators**

**Spring Boot** предоставляет множество встроенных **health indicators**:**

- **DiskSpaceHealthIndicator**: Проверка свободного места на диске
- **DataSourceHealthIndicator**: Проверка подключения к БД
- **RedisHealthIndicator**: Проверка подключения к **Redis**
- **MongoHealthIndicator**: Проверка подключения к **MongoDB**
- **RabbitHealthIndicator**: Проверка подключения к **RabbitMQ**

### **Custom Health Indicators**

```java
// Кастомный HealthIndicator с деталями статуса (up/down)
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // Проверка состояния
        boolean isHealthy = checkSystemHealth();
        
        if (isHealthy) {
            return Health.up()
                .withDetail("status", "System is healthy")
                .withDetail("timestamp", System.currentTimeMillis())
                .build();
        } else {
            return Health.down()
                .withDetail("status", "System is down")
                .withDetail("error", "Connection failed")
                .build();
        }
    }
    
    private boolean checkSystemHealth() {
        // Логика проверки
        return true;
    }
}
```

### **Composite Health Indicators**

```java
// Проверка доступности БД через DataSource
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                    .withDetail("database", "Available")
                    .withDetail("validationQuery", "isValid()")
                    .build();
            } else {
                return Health.down()
                    .withDetail("database", "Unavailable")
                    .build();
            }
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "Error")
                .withException(e)
                .build();
        }
    }
}
```

## Metrics Endpoints

### Базовое использование

```properties
# Включение metrics endpoint
management.endpoint.metrics.enabled=true
```

### Встроенные метрики

**Spring Boot** автоматически собирает множество метрик:**

- **JVM метрики**: память, потоки, классы
- **HTTP метрики**: запросы, ответы, ошибки
- **Database метрики**: подключения, запросы
- **Custom метрики**: пользовательские метрики

### Просмотр метрик

```bash
# Все метрики
curl http://localhost:8080/actuator/metrics

# Конкретная метрика
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Метрика с тегами
curl http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/api/users
```

### **Custom Metrics**

```java
// Счётчик и таймер создания пользователей через Micrometer
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    private final Counter userCreatedCounter;
    private final Timer userCreationTimer;
    private final MeterRegistry meterRegistry;
    
    public UserService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
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

### **Gauge Metrics**

```java
// Gauge для отображения числа активных пользователей
@Component
public class ActiveUsersGauge {
    
    private final AtomicInteger activeUsers = new AtomicInteger(0);
    
    public ActiveUsersGauge(MeterRegistry meterRegistry) {
        Gauge.builder("users.active", activeUsers, AtomicInteger::get)
            .description("Number of active users")
            .register(meterRegistry);
    }
    
    public void increment() {
        activeUsers.incrementAndGet();
    }
    
    public void decrement() {
        activeUsers.decrementAndGet();
    }
}
```

## Info Endpoint

### Настройка **Info**

```properties
# Включение info endpoint
management.endpoint.info.enabled=true

# Информация из application.properties
info.app.name=My Application
info.app.description=My Application Description
info.app.version=1.0.0
info.app.encoding=@project.build.sourceEncoding@
info.java.version=@java.version@
```

### **Custom Info Contributor**

```java
// Добавление кастомной информации в /actuator/info
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

@Component
public class CustomInfoContributor implements InfoContributor {
    
    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("custom", Map.of(
            "environment", System.getenv("ENV"),
            "timestamp", System.currentTimeMillis(),
            "uptime", getUptime()
        ));
    }
    
    private long getUptime() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }
}
```

## Custom Endpoints

### Создание **Custom Endpoint**

```java
// Кастомный endpoint с read/write операциями
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "custom")
public class CustomEndpoint {
    
    @ReadOperation
    public Map<String, Object> custom() {
        Map<String, Object> info = new HashMap<>();
        info.put("status", "OK");
        info.put("timestamp", System.currentTimeMillis());
        info.put("customData", "Some custom data");
        return info;
    }
    
    @WriteOperation
    public void customOperation(String action) {
        // Выполнение операции
        System.out.println("Custom operation: " + action);
    }
}
```

### **Web Endpoint**

```java
// Кастомный HTTP endpoint с read/write/delete операциями
@Component
@WebEndpoint(id = "customweb")
public class CustomWebEndpoint {
    
    @ReadOperation
    public Map<String, Object> read() {
        return Map.of("message", "Hello from custom web endpoint");
    }
    
    @WriteOperation
    public Map<String, Object> write(String data) {
        return Map.of("received", data, "status", "processed");
    }
    
    @DeleteOperation
    public Map<String, Object> delete() {
        return Map.of("status", "deleted");
    }
}
```

## Security

### Защита **Endpoints**

```java
// Ограничение доступа к actuator: health/info — всем, остальное — ACTUATOR
@Configuration
public class ActuatorSecurityConfig {
    
    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .requestMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests(requests -> 
                requests
                    .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                    .anyRequest().hasRole("ACTUATOR")
            )
            .httpBasic();
        return http.build();
    }
}
```

### Конфигурация через **properties**

```properties
# Базовый путь для security
management.endpoints.web.base-path=/actuator

# Включение security
management.security.enabled=true
```

## Prometheus Integration

### Настройка **Prometheus**

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

```properties
# Включение Prometheus endpoint
management.endpoints.web.exposure.include=prometheus,health,metrics
management.metrics.export.prometheus.enabled=true
```

### Использование

```bash
# Prometheus метрики
curl http://localhost:8080/actuator/prometheus
```

## Лучшие практики

### 1. Ограничивайте доступ к **endpoints**

```properties
# ✅ Хорошо
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

### 2. Используйте **security** для **production**

```java
// ✅ Хорошо
@Bean
public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) {
    // Защита endpoints
}
```

### 3. Создавайте **custom health indicators**

```java
// ✅ Хорошо
@Component
public class CustomHealthIndicator implements HealthIndicator {
    // Проверка критических компонентов
}
```

### 4. Используйте метрики для мониторинга

```java
// ✅ Хорошо
Counter.builder("users.created")
    .description("Number of users created")
    .register(meterRegistry);
```

### 5. Настраивайте **info endpoint**

```properties
# ✅ Хорошо
info.app.name=My Application
info.app.version=1.0.0
```

## Расширенные **Health Indicators**

### **Reactive Health Indicators**

```java
// Реактивная проверка БД через R2DBC
@Component
public class ReactiveDatabaseHealthIndicator implements ReactiveHealthIndicator {
    
    @Autowired
    private R2dbcEntityTemplate template;
    
    @Override
    public Mono<Health> health() {
        return template.getDatabaseClient()
            .sql("SELECT 1")
            .fetch()
            .rowsUpdated()
            .map(rows -> Health.up()
                .withDetail("database", "Reactive Database")
                .withDetail("status", "Connected")
                .build())
            .onErrorResume(ex -> Mono.just(Health.down()
                .withDetail("error", ex.getMessage())
                .build()));
    }
}
```

### **Health Groups**

```properties
# Группировка health indicators
management.endpoint.health.group.custom.include=db,diskSpace,ping
management.endpoint.health.group.custom.show-details=always
```

```java
// Регистрация групп health indicators
@Configuration
public class HealthGroupConfig {
    
    @Bean
    public HealthContributorRegistry healthContributorRegistry() {
        return new HealthContributorRegistry() {
            // Регистрация health contributors
        };
    }
}
```

### **Health Status Aggregation**

```java
// Агрегация статусов нескольких HealthIndicator
@Component
public class AggregatedHealthIndicator implements HealthIndicator {
    
    @Autowired
    private List<HealthIndicator> healthIndicators;
    
    @Override
    public Health health() {
        Map<String, Health> healths = healthIndicators.stream()
            .collect(Collectors.toMap(
                indicator -> indicator.getClass().getSimpleName(),
                HealthIndicator::health
            ));
        
        boolean allUp = healths.values().stream()
            .allMatch(h -> h.getStatus().equals(Status.UP));
        
        Health.Builder builder = allUp ? Health.up() : Health.down();
        healths.forEach((name, health) -> 
            builder.withDetail(name, health.getDetails())
        );
        
        return builder.build();
    }
}
```

## Расширенные **Metrics**

### **Custom Meter Registry**

```java
// Общие теги для всех метрик (application, environment)
@Configuration
public class CustomMeterRegistryConfig {
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
            .commonTags("application", "my-app")
            .commonTags("environment", System.getenv("ENV"));
    }
}
```

### **Timed Annotations**

```java
// Измерение времени выполнения метода через @Timed
@Service
public class TimedService {
    
    @Timed(value = "service.method", description = "Time taken to execute method")
    public void executeMethod() {
        // Метод с автоматическим измерением времени
    }
    
    @Timed(value = "service.async", longTask = true)
    @Async
    public CompletableFuture<String> asyncMethod() {
        return CompletableFuture.completedFuture("result");
    }
}
```

### **Distribution Statistics**

```java
// Настройка перцентилей и гистограмм для метрик
@Configuration
public class MetricsConfig {
    
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCustomizer() {
        return registry -> registry.config()
            .meterFilter(new MeterFilter() {
                @Override
                public DistributionStatisticConfig configure(
                        Meter.Id id, DistributionStatisticConfig config) {
                    return DistributionStatisticConfig.builder()
                        .percentiles(0.5, 0.95, 0.99)
                        .percentilesHistogram(true)
                        .build()
                        .merge(config);
                }
            });
    }
}
```

### **Sliding Window Metrics**

```java
// Метрики со скользящим окном: Counter и Timer через Micrometer
@Service
public class SlidingWindowService {

    private final MeterRegistry meterRegistry;
    private final Counter requestCounter;
    private final Timer requestTimer;
    
    public SlidingWindowService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestCounter = Counter.builder("requests.total")
            .description("Total number of requests")
            .register(meterRegistry);
        this.requestTimer = Timer.builder("requests.duration")
            .description("Request duration")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);
    }
    
    public void processRequest() {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            // Обработка запроса
            requestCounter.increment();
        } finally {
            sample.stop(requestTimer);
        }
    }
}
```

## Интеграция с различными системами мониторинга

### **InfluxDB**

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-influx</artifactId>
</dependency>
```

```properties
management.metrics.export.influx.uri=http://localhost:8086
management.metrics.export.influx.db=mydb
management.metrics.export.influx.user=admin
management.metrics.export.influx.password=admin
management.metrics.export.influx.step=10s
```

### **Graphite**

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-graphite</artifactId>
</dependency>
```

```properties
management.metrics.export.graphite.host=localhost
management.metrics.export.graphite.port=2004
management.metrics.export.graphite.protocol=plaintext
management.metrics.export.graphite.step=10s
```

### **CloudWatch**

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-cloudwatch2</artifactId>
</dependency>
```

```properties
management.metrics.export.cloudwatch.namespace=MyApp
management.metrics.export.cloudwatch.step=60s
```

### **Datadog**

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-datadog</artifactId>
</dependency>
```

```properties
management.metrics.export.datadog.api-key=your-api-key
management.metrics.export.datadog.application-key=your-app-key
management.metrics.export.datadog.step=10s
```

## Расширенные **Endpoints**

### **JMX Endpoint**

```java
// Кастомный endpoint, доступный через JMX
@Component
@JmxEndpoint(id = "customjmx")
public class CustomJmxEndpoint {
    
    @ReadOperation
    public String read() {
        return "JMX endpoint data";
    }
    
    @WriteOperation
    public void write(String data) {
        // Запись данных
    }
}
```

### **Conditional Endpoints**

```java
// Endpoint включается только при management.endpoint.custom.enabled=true
@Component
@ConditionalOnProperty(name = "management.endpoint.custom.enabled", havingValue = "true")
@Endpoint(id = "custom")
public class ConditionalCustomEndpoint {
    
    @ReadOperation
    public Map<String, Object> read() {
        return Map.of("status", "enabled");
    }
}
```

### **Endpoint Filters**

```java
// Фильтр исключения endpoint'ов (например shutdown) из экспозиции
@Component
public class EndpointFilter implements EndpointFilter<ExposableEndpoint<?>> {

    @Override
    public boolean match(ExposableEndpoint<?> endpoint) {
        // Фильтрация endpoints
        return !endpoint.getId().equals("shutdown");
    }
}
```

## Логирование и **Tracing**

### **Loggers Endpoint**

```properties
# Включение loggers endpoint
management.endpoint.loggers.enabled=true
management.endpoints.web.exposure.include=loggers
```

```java
// Чтение и изменение уровней логгеров через endpoint
@Component
@Endpoint(id = "customloggers")
public class CustomLoggersEndpoint {
    
    @ReadOperation
    public Map<String, Object> getLoggers() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Map<String, Object> loggers = new HashMap<>();
        
        loggerContext.getLoggerList().forEach(logger -> {
            loggers.put(logger.getName(), Map.of(
                "level", logger.getLevel() != null ? logger.getLevel().toString() : "null",
                "effectiveLevel", logger.getEffectiveLevel().toString()
            ));
        });
        
        return loggers;
    }
    
    @WriteOperation
    public void setLoggerLevel(String name, String level) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = loggerContext.getLogger(name);
        logger.setLevel(Level.valueOf(level));
    }
}
```

### **HTTP Tracing**

```properties
# Включение HTTP tracing
management.tracing.sampling.probability=1.0
management.tracing.http.enabled=true
```

```java
// Конфигурация трейсинга HTTP-запросов
@Configuration
public class TracingConfig {
    
    @Bean
    public HttpExchangeTracer httpExchangeTracer() {
        return new HttpExchangeTracer();
    }
}
```

## Управление приложением

### **Shutdown Endpoint**

```properties
# Включение shutdown endpoint
management.endpoint.shutdown.enabled=true
management.endpoints.web.exposure.include=shutdown
```

```java
// Endpoint для корректного завершения приложения (graceful shutdown)
@Component
@Endpoint(id = "graceful-shutdown")
public class GracefulShutdownEndpoint {

    @Autowired
    private ConfigurableApplicationContext context;
    
    @WriteOperation
    public Map<String, String> shutdown() {
        Map<String, String> result = new HashMap<>();
        try {
            // Graceful shutdown
            context.close();
            result.put("status", "shutdown");
        } catch (Exception e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        return result;
    }
}
```

### **Environment Endpoint**

```properties
# Включение environment endpoint
management.endpoint.env.enabled=true
management.endpoints.web.exposure.include=env
```

```java
// Endpoint с информацией о профилях и окружении
@Component
@Endpoint(id = "customenv")
public class CustomEnvironmentEndpoint {
    
    @Autowired
    private Environment environment;
    
    @ReadOperation
    public Map<String, Object> environment() {
        Map<String, Object> env = new HashMap<>();
        env.put("activeProfiles", Arrays.asList(environment.getActiveProfiles()));
        env.put("defaultProfiles", Arrays.asList(environment.getDefaultProfiles()));
        return env;
    }
}
```

## Производительность и оптимизация

### Метрики производительности

```java
// Метрики кеша и времени запросов к БД
@Component
public class PerformanceMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter cacheHits;
    private final Counter cacheMisses;
    private final Timer queryTimer;
    
    public PerformanceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.cacheHits = Counter.builder("cache.hits")
            .description("Cache hits")
            .register(meterRegistry);
        this.cacheMisses = Counter.builder("cache.misses")
            .description("Cache misses")
            .register(meterRegistry);
        this.queryTimer = Timer.builder("database.queries")
            .description("Database query time")
            .register(meterRegistry);
    }
    
    public void recordCacheHit() {
        cacheHits.increment();
    }
    
    public void recordCacheMiss() {
        cacheMisses.increment();
    }
    
    public <T> T timeQuery(Supplier<T> query) {
        return queryTimer.record(query);
    }
}
```

### Метрики памяти

```java
// Gauge для heap memory (used/max)
@Component
public class MemoryMetrics {
    
    private final MeterRegistry meterRegistry;
    
    public MemoryMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        registerMemoryMetrics();
    }
    
    private void registerMemoryMetrics() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        Gauge.builder("jvm.memory.heap.used", memoryBean, 
            bean -> bean.getHeapMemoryUsage().getUsed())
            .description("Used heap memory")
            .register(meterRegistry);
        
        Gauge.builder("jvm.memory.heap.max", memoryBean,
            bean -> bean.getHeapMemoryUsage().getMax())
            .description("Max heap memory")
            .register(meterRegistry);
    }
}
```

## Безопасность **Actuator**

### **Role-based Access Control**

```java
@Configuration
public class ActuatorSecurityConfig {
    
    @Bean
    public SecurityFilterChain actuatorSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .requestMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeHttpRequests(requests -> 
                requests
                    .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                    .requestMatchers(EndpointRequest.to("metrics", "prometheus"))
                        .hasRole("MONITORING")
                    .requestMatchers(EndpointRequest.to("env", "configprops"))
                        .hasRole("ADMIN")
                    .requestMatchers(EndpointRequest.to("shutdown"))
                        .hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .httpBasic();
        return http.build();
    }
}
```

### **IP-based Access Control**

```java
// Ограничение доступа к /actuator по списку разрешённых IP
@Component
public class ActuatorAccessControl implements HandlerInterceptor {
    
    private static final List<String> ALLOWED_IPS = List.of(
        "127.0.0.1", "::1", "10.0.0.0/8"
    );
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
            HttpServletResponse response, Object handler) {
        String clientIp = getClientIp(request);
        if (request.getRequestURI().startsWith("/actuator")) {
            if (!isAllowed(clientIp)) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                return false;
            }
        }
        return true;
    }
    
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private boolean isAllowed(String ip) {
        return ALLOWED_IPS.stream().anyMatch(allowed -> matches(ip, allowed));
    }
    
    private boolean matches(String ip, String pattern) {
        // Простая проверка IP/CIDR
        return ip.equals(pattern) || pattern.contains("/") && 
            matchesCidr(ip, pattern);
    }
    
    private boolean matchesCidr(String ip, String cidr) {
        // Реализация проверки CIDR
        return true;
    }
}
```

## Кастомизация **Actuator**

### **Custom Endpoint Paths**

```properties
# Кастомные пути для endpoints
management.endpoints.web.path-mapping.health=healthcheck
management.endpoints.web.path-mapping.metrics=stats
```

### **Custom Endpoint Response**

```java
// Endpoint с кастомными заголовками и JSON-ответом
@Component
@Endpoint(id = "customresponse")
public class CustomResponseEndpoint {
    
    @ReadOperation(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> read() {
        Map<String, Object> response = Map.of(
            "status", "ok",
            "timestamp", System.currentTimeMillis(),
            "data", "custom data"
        );
        return ResponseEntity.ok()
            .header("X-Custom-Header", "value")
            .body(response);
    }
}
```

## Интеграция с **Kubernetes**

### **Kubernetes Probes**

```properties
# Настройка для Kubernetes
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
management.endpoint.health.probes.enabled=true
```

```java
// Health indicator для liveness/readiness в Kubernetes
@Component
public class KubernetesHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        // Проверка готовности для Kubernetes
        boolean ready = checkReadiness();
        return ready ? Health.up() : Health.down();
    }
    
    private boolean checkReadiness() {
        // Проверка зависимостей
        return true;
    }
}
```


## Заключение

**Spring Actuator** предоставляет мощные инструменты для мониторинга и управления **Spring Boot** приложениями. Правильное использование **health checks**, **metrics**, **custom endpoints**, **security**, интеграций с различными системами мониторинга и других продвинутых возможностей критично для создания **production-ready** приложений с полной **observability**.

## Дополнительные ресурсы

- [**Spring Boot Actuator** Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [**Spring Cloud** Sleuth](https://github.com/spring-cloud/spring-cloud-sleuth)
