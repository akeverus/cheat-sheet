---
title: "OpenTelemetry: Observability в Java"
description: "Комплексное руководство по использованию OpenTelemetry — открытого стандарта для сбора и экспорта телеметрии (метрики, трейсы, логи) в Java приложениях."
tags:
  - libraries
  - java
  - java-opentelemetry
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# OpenTelemetry: **Observability** в **Java**

**Комплексное руководство по использованию `OpenTelemetry` — открытого стандарта для сбора и экспорта телеметрии (**метрики, трейсы, логи**) в `Java` приложениях.**

## Полезные ссылки

### Официальная документация
- [OpenTelemetry](https://opentelemetry.io/) — официальный сайт
- [OpenTelemetry Java](https://opentelemetry.io/docs/instrumentation/java/) — **Java** документация
- [OpenTelemetry GitHub](https://github.com/open-telemetry/opentelemetry-java) — репозиторий проекта

### Интеграция
- [Spring Boot OpenTelemetry](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.micrometer-tracing) — **Spring Boot starter**
- [Micrometer Bridge](https://micrometer.io/docs/tracing) — интеграция с **Micrometer**
- [OpenTelemetry Registry](https://opentelemetry.io/ecosystem/registry/) — реестр компонентов

## Содержание

- [Введение в OpenTelemetry](#введение-в-opentelemetry)
  - [Почему OpenTelemetry?](#почему-opentelemetry)
  - [Три столпа Observability](#три-столпа-observability)
  - [Архитектура OpenTelemetry](#архитектура-opentelemetry)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Базовая настройка](#базовая-настройка)
- [Tracing (**Трейсинг**)](#tracing-трейсинг)
  - [Основы Tracing](#основы-tracing)
  - [Атрибуты и события](#атрибуты-и-события)
  - [Context Propagation](#context-propagation)
- [Metrics (**Метрики**)](#metrics-метрики)
  - [Основы Metrics](#основы-metrics)
  - [Продвинутые метрики](#продвинутые-метрики)
- [Logs (**Логирование**)](#logs-логирование)
  - [OpenTelemetry Logging](#opentelemetry-logging)
  - [Интеграция с существующими логгерами](#интеграция-с-существующими-логгерами)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Автоматическая конфигурация](#автоматическая-конфигурация)
- [application.yml](#applicationyml)
- [Jaeger настройки](#jaeger-настройки)
- [OTLP настройки](#otlp-настройки)
- [Prometheus метрики](#prometheus-метрики)
  - [Spring Boot конфигурация](#spring-boot-конфигурация)
  - [Аспект для автоматического трейсинга](#аспект-для-автоматического-трейсинга)
  - [REST контроллеры с трейсингом](#rest-контроллеры-с-трейсингом)
- [Экспорт данных](#экспорт-данных)
  - [Jaeger](#jaeger)
  - [Prometheus](#prometheus)
  - [OTLP (**OpenTelemetry Protocol**)](#otlp-opentelemetry-protocol)
  - [ELK Stack](#elk-stack)
- [Best practices](#best-practices)
  - [1. Правильная структура трейсов](#1-правильная-структура-трейсов)
  - [2. Эффективные метрики](#2-эффективные-метрики)
  - [3. Context propagation](#3-context-propagation)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
  - [Преимущества OpenTelemetry](#преимущества-opentelemetry)
  - [Основные паттерны использования](#основные-паттерны-использования)
  - [Когда использовать OpenTelemetry](#когда-использовать-opentelemetry)
  - [Сравнение с альтернативами](#сравнение-с-альтернативами)

## Введение в **OpenTelemetry**

**OpenTelemetry** — это набор инструментов, **API** и **SDK** для сбора и экспорта телеметрии (**метрики, трейсы, логи**) для анализа поведения и производительности приложений. Это **vendor-neutral** открытый стандарт, поддерживаемый **CNCF**.

### Почему **OpenTelemetry**?

**OpenTelemetry** предлагает множество преимуществ:**

1. **Vendor neutral** — Не зависит от конкретных платформ мониторинга
2. **Единый стандарт** — Общий формат для метрик, трейсов и логов
3. **Автоматическая инструментация** — Многие фреймворки поддерживаются **out-`of-the`-box**
4. **Context propagation** — Распространение контекста между сервисами
5. **Extensible** — Легко добавить кастомную инструментацию
6. **CNCF project** — Поддерживается ведущими компаниями
7. **Широкая экосистема** — Интеграции с популярными инструментами
8. **Future-proof** — Стандарт для облачных и микросервисных архитектур

### Три столпа **Observability**

**Tracing** — Отслеживание пути запроса через систему
**Metrics** — Количественные измерения производительности
**Logs** — Структурированные текстовые записи о событиях

### Архитектура **OpenTelemetry**

```text
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Application   │────│   OpenTelemetry │────│   Observability │
│                 │    │     SDK         │    │   Platform      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                       ┌──────┴──────┐
                       │  Exporters  │
                       └─────────────┘
                              │
                       ┌──────┴──────┐
                       │   Signals   │
                       │ • Tracing   │
                       │ • Metrics   │
                       │ • Logs      │
                       └─────────────┘
```

## Установка и настройка

### **Maven**

Зависимости **Maven** для **OpenTelemetry SDK** (**tracing, metrics**).

```xml
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
    <version>1.32.0</version>
</dependency>

<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
    <version>1.32.0</version>
</dependency>

<!-- Для Spring Boot -->
<dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-spring-boot-starter</artifactId>
    <version>1.32.0</version>
</dependency>

<!-- Экспортеры -->
<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-jaeger</artifactId>
    <version>1.32.0</version>
</dependency>

<dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
    <version>1.32.0</version>
</dependency>
```

### **Gradle**

```kotlin
dependencies {
    implementation("io.opentelemetry:opentelemetry-api:1.32.0")
    implementation("io.opentelemetry:opentelemetry-sdk:1.32.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:1.32.0")
    implementation("io.opentelemetry:opentelemetry-exporter-jaeger:1.32.0")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.32.0")
}
```

### Базовая настройка

```java
@Configuration
public class OpenTelemetryConfiguration {

    @Bean
    public OpenTelemetry openTelemetry() {
        // Создание SDK
        SDKBuilder sdkBuilder = OpenTelemetrySdk.builder();

        // Настройка tracer provider
        TracerProvider tracerProvider = TracerProvider.builder()
            .addSpanProcessor(SimpleSpanProcessor.create(jaegerExporter()))
            .build();

        sdkBuilder.setTracerProvider(tracerProvider);

        // Настройка meter provider
        MeterProvider meterProvider = MeterProvider.builder()
            .registerMetricReader(PeriodicMetricReader.builder(prometheusExporter()).build())
            .build();

        sdkBuilder.setMeterProvider(meterProvider);

        return sdkBuilder.buildAndRegisterGlobal();
    }

    @Bean
    public SpanExporter jaegerExporter() {
        return JaegerGrpcSpanExporter.builder()
            .setEndpoint("http://localhost:14250")
            .build();
    }

    @Bean
    public MetricExporter prometheusExporter() {
        return PrometheusMetricExporter.builder().build();
    }
}
```

## **Tracing** (**Трейсинг**)

### Основы **Tracing**

```java
/
 * Сервис для демонстрации ручного создания span в OpenTelemetry
 * Span представляет единицу работы в distributed trace
 */
@Service
public class TracingService {

    // Tracer - основной интерфейс для создания span в OpenTelemetry
    private final Tracer tracer;

    /
     * Конструктор с инициализацией Tracer из OpenTelemetry
     * @param openTelemetry OpenTelemetry экземпляр для получения Tracer
     */
    public TracingService(OpenTelemetry openTelemetry) {
        // getTracer() создает Tracer с указанным именем инструментации
        // Имя используется для группировки span в трейсе
        this.tracer = openTelemetry.getTracer("com.example.service");
    }

    /
     * Выполнение операции с ручным созданием span
     * Демонстрирует создание span, добавление атрибутов и событий
     */
    public void performOperation() {
        // Создание span - span представляет единицу работы в трейсе
        Span span = tracer.spanBuilder("operation")  // Создаем builder для span с именем "operation"
            .setAttribute("operation.type", "database")    // Добавляем атрибут типа операции
            .setAttribute("operation.table", "users")      // Добавляем атрибут таблицы
            .startSpan();  // Запускаем span (начинаем отсчет времени)

        // makeCurrent() делает span текущим в контексте выполнения
        // Scope автоматически закрывается при выходе из try-with-resources
        try (Scope scope = span.makeCurrent()) {
            // Выполнение операции - весь код внутри scope будет частью этого span
            doWork();

            // Добавление событий - события отмечают важные моменты в выполнении операции
            span.addEvent("Starting database query");  // Событие начала запроса к БД
            executeQuery();                            // Выполняем запрос
            span.addEvent("Query completed");          // Событие завершения запроса
            // События помогают понять последовательность действий в span

        } catch (Exception e) {
            // Запись ошибки - если произошло исключение, записываем его в span
            span.recordException(e);  // Записываем исключение в span для анализа
            span.setStatus(StatusCode.ERROR, "Operation failed: " + e.getMessage());  // Устанавливаем статус ERROR
            // Статус ERROR указывает что операция завершилась с ошибкой
            throw e;  // Пробрасываем исключение дальше
        } finally {
            span.end();  // Завершаем span - обязательно вызывать в finally
            // end() останавливает отсчет времени и финализирует span
        }
    }

    public void nestedOperations() {
        Span parentSpan = tracer.spanBuilder("parent.operation").startSpan();

        try (Scope parentScope = parentSpan.makeCurrent()) {
            // Родительская операция
            doInitialWork();

            // Дочерняя операция
            Span childSpan = tracer.spanBuilder("child.operation")
                .setParent(Context.current().with(parentSpan))
            .startSpan();

            try (Scope childScope = childSpan.makeCurrent()) {
                doChildWork();
        } finally {
                childSpan.end();
            }

        } finally {
            parentSpan.end();
        }
    }

    private void doWork() {
        // Имитация работы
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void executeQuery() {
        // Имитация запроса к БД
    }

    private void doInitialWork() {
        // Имитация начальной работы
    }

    private void doChildWork() {
        // Имитация дочерней работы
    }
}
```

### Атрибуты и события

```java
@Service
public class TracingAttributesService {

    private final Tracer tracer;

    public TracingAttributesService(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("com.example.attributes");
    }

    public void httpRequestTracing(HttpServletRequest request, long responseTime) {
        Span span = tracer.spanBuilder("http.request")
            .setAttribute("http.method", request.getMethod())
            .setAttribute("http.url", request.getRequestURL().toString())
            .setAttribute("http.user_agent", request.getHeader("User-Agent"))
            .setAttribute("http.status_code", 200)
            .setAttribute("http.response_time", responseTime)
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Обработка запроса
            processHttpRequest(request);
        } finally {
            span.end();
        }
    }

    public void databaseOperationTracing(String table, String operation, int affectedRows) {
        Span span = tracer.spanBuilder("database.operation")
            .setAttribute("db.system", "postgresql")
            .setAttribute("db.name", "myapp")
            .setAttribute("db.table", table)
            .setAttribute("db.operation", operation)
            .setAttribute("db.affected_rows", affectedRows)
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            span.addEvent("Executing query", Attributes.of(
                AttributeKey.stringKey("query.type"), operation,
                AttributeKey.stringKey("table.name"), table
            ));

            // Выполнение операции
            executeDatabaseOperation(table, operation);

            span.addEvent("Query completed", Attributes.of(
                AttributeKey.longKey("rows.affected"), (long) affectedRows
            ));

        } catch (Exception e) {
            span.recordException(e, Attributes.of(
                AttributeKey.stringKey("error.type"), e.getClass().getSimpleName(),
                AttributeKey.stringKey("error.message"), e.getMessage()
            ));
            throw e;
            } finally {
            span.end();
        }
    }

    public void businessLogicTracing(String userId, String operation) {
        Span span = tracer.spanBuilder("business.logic")
            .setAttribute("business.user_id", userId)
            .setAttribute("business.operation", operation)
            .setAttribute("business.service", "user-service")
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Бизнес логика
            performBusinessLogic(userId, operation);

            // Добавление тегов на основе результата
            span.setAttribute("business.result", "success");
            span.setAttribute("business.duration", System.currentTimeMillis() - span.toSpanData().getStartEpochNanos() / 1_000_000);

        } catch (Exception e) {
            span.setAttribute("business.result", "error");
            span.setAttribute("business.error_type", e.getClass().getSimpleName());
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    private void processHttpRequest(HttpServletRequest request) {
        // Обработка HTTP запроса
    }

    private void executeDatabaseOperation(String table, String operation) {
        // Выполнение операции с БД
    }

    private void performBusinessLogic(String userId, String operation) {
        // Выполнение бизнес логики
    }
}
```

### **Context Propagation**

```java
@Service
@Slf4j
public class ContextPropagationService {

    private final Tracer tracer;

    public ContextPropagationService(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("com.example.propagation");
    }

    public void asyncOperationWithContext() {
        Span parentSpan = tracer.spanBuilder("async.operation").startSpan();

        try (Scope scope = parentSpan.makeCurrent()) {
            // Запуск асинхронной операции с передачей контекста
            CompletableFuture.runAsync(this::asyncTask)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Async operation failed", throwable);
                    }
                });
        } finally {
            parentSpan.end();
        }
    }

    private void asyncTask() {
        // Получение span из контекста
        Span currentSpan = Span.current();

        if (currentSpan != null) {
            try (Scope scope = currentSpan.makeCurrent()) {
                Span childSpan = tracer.spanBuilder("async.task")
                    .setParent(Context.current().with(currentSpan))
                    .startSpan();

                try (Scope childScope = childSpan.makeCurrent()) {
                    // Выполнение асинхронной задачи
                    performAsyncWork();
                } finally {
                    childSpan.end();
                }
            }
        }
    }

    public void distributedTracing(String traceId, String spanId) {
        // Восстановление контекста из заголовков
        TextMapPropagator propagator = W3CTraceContextPropagator.getInstance();

        // Имитация получения заголовков
        Map<String, String> headers = Map.of(
            "traceparent", "00-" + traceId + "-" + spanId + "-01"
        );

        Context extractedContext = propagator.extract(Context.current(), headers,
            (carrier, key) -> carrier.get(key));

        Span span = tracer.spanBuilder("distributed.operation")
            .setParent(extractedContext)
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Обработка входящего запроса
            processDistributedRequest();
        } finally {
            span.end();
        }
    }

    public Map<String, String> injectContextForOutgoingCall() {
        // Инъекция контекста в заголовки для исходящего вызова
        TextMapPropagator propagator = W3CTraceContextPropagator.getInstance();
        Map<String, String> headers = new HashMap<>();

        propagator.inject(Context.current(), headers,
            (carrier, key, value) -> carrier.put(key, value));

        return headers;
    }

    private void performAsyncWork() {
        // Имитация асинхронной работы
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processDistributedRequest() {
        // Обработка распределенного запроса
    }
}
```

## **Metrics** (**Метрики**)

### Основы **Metrics**

```java
@Service
public class MetricsService {

    private final Meter meter;

    public MetricsService(OpenTelemetry openTelemetry) {
        this.meter = openTelemetry.getMeter("com.example.metrics");
    }

    public void recordMetrics() {
        // Counter - монотонно возрастающий счетчик
        LongCounter requestCounter = meter.counterBuilder("http_requests_total")
            .setDescription("Total number of HTTP requests")
            .setUnit("1")
            .build();

        requestCounter.add(1, Attributes.of(
            AttributeKey.stringKey("method"), "GET",
            AttributeKey.stringKey("endpoint"), "/api/users"
        ));

        // UpDownCounter - может расти и падать
        UpDownCounter activeConnections = meter.upDownCounterBuilder("active_connections")
            .setDescription("Number of active connections")
            .setUnit("1")
            .build();

        activeConnections.add(5, Attributes.of(
            AttributeKey.stringKey("service"), "user-service"
        ));

        // Histogram - распределение значений
        DoubleHistogram requestDuration = meter.histogramBuilder("http_request_duration_seconds")
            .setDescription("HTTP request duration in seconds")
            .setUnit("s")
            .build();

        requestDuration.record(0.145, Attributes.of(
            AttributeKey.stringKey("method"), "GET",
            AttributeKey.stringKey("status"), "200"
        ));

        // Observable Gauge - асинхронное измерение
        meter.gaugeBuilder("system_memory_usage")
            .setDescription("System memory usage")
            .setUnit("bytes")
            .buildWithCallback(measurement -> {
                measurement.record(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
                    Attributes.of(AttributeKey.stringKey("type"), "heap"));
            });
    }

    public void businessMetrics() {
        // Метрики для бизнес логики
        LongCounter ordersCreated = meter.counterBuilder("orders_created_total")
            .setDescription("Total number of orders created")
            .build();

        DoubleHistogram orderValue = meter.histogramBuilder("order_value")
            .setDescription("Order value distribution")
            .setUnit("currency")
            .build();

        // Имитация создания заказа
        ordersCreated.add(1, Attributes.of(
            AttributeKey.stringKey("customer_type"), "premium",
            AttributeKey.stringKey("payment_method"), "credit_card"
        ));

        orderValue.record(99.99, Attributes.of(
            AttributeKey.stringKey("currency"), "USD",
            AttributeKey.stringKey("category"), "electronics"
            ));
    }
}
```

### Продвинутые метрики

```java
@Service
@Slf4j
public class AdvancedMetricsService {

    private final Meter meter;

    public AdvancedMetricsService(OpenTelemetry openTelemetry) {
        this.meter = openTelemetry.getMeter("com.example.advanced.metrics");
    }

    public void setupSystemMetrics() {
        // CPU usage gauge
        meter.gaugeBuilder("system_cpu_usage")
            .setDescription("System CPU usage percentage")
            .setUnit("%")
            .buildWithCallback(measurement -> {
                // Реальная реализация получения CPU usage
                double cpuUsage = getCpuUsage();
                measurement.record(cpuUsage, Attributes.of(
                    AttributeKey.stringKey("host"), getHostName()
                ));
            });

        // Memory usage gauge
        meter.gaugeBuilder("system_memory_usage")
            .setDescription("System memory usage")
            .setUnit("bytes")
            .buildWithCallback(measurement -> {
                Runtime runtime = Runtime.getRuntime();
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                long maxMemory = runtime.maxMemory();

                measurement.record(usedMemory, Attributes.of(
                    AttributeKey.stringKey("type"), "heap",
                    AttributeKey.stringKey("host"), getHostName()
                ));

                measurement.record(maxMemory - usedMemory, Attributes.of(
                    AttributeKey.stringKey("type"), "heap_free",
                    AttributeKey.stringKey("host"), getHostName()
                ));
            });

        // Disk usage gauge
        meter.gaugeBuilder("system_disk_usage")
            .setDescription("Disk usage")
            .setUnit("bytes")
            .buildWithCallback(measurement -> {
                // Имитация получения дискового пространства
                measurement.record(1024 * 1024 * 1024, Attributes.of( // 1GB
                    AttributeKey.stringKey("mount_point"), "/",
                    AttributeKey.stringKey("filesystem"), "ext4"
                ));
            });
    }

    public void setupBusinessMetrics() {
        // User engagement metrics
        LongCounter userLogins = meter.counterBuilder("user_logins_total")
            .setDescription("Total number of user logins")
            .build();

        LongCounter userRegistrations = meter.counterBuilder("user_registrations_total")
            .setDescription("Total number of user registrations")
            .build();

        // Order metrics
        DoubleHistogram orderProcessingTime = meter.histogramBuilder("order_processing_time")
            .setDescription("Time taken to process orders")
            .setUnit("ms")
            .build();

        LongCounter ordersByStatus = meter.counterBuilder("orders_by_status_total")
            .setDescription("Orders grouped by status")
            .build();

        // Revenue metrics
        DoubleCounter revenue = meter.counterBuilder("revenue_total")
            .setDescription("Total revenue")
            .setUnit("currency")
            .build();
    }

    public void recordUserActivity(String userId, String action) {
        meter.counterBuilder("user_activity_total")
            .setDescription("User activity counter")
            .build()
            .add(1, Attributes.of(
                AttributeKey.stringKey("user_id"), userId,
                AttributeKey.stringKey("action"), action,
                AttributeKey.stringKey("timestamp"), Instant.now().toString()
            ));
    }

    public void recordPerformanceMetrics(String operation, long duration, boolean success) {
        // Histogram для duration
        meter.histogramBuilder("operation_duration")
            .setDescription("Operation duration distribution")
            .setUnit("ms")
            .build()
            .record(duration, Attributes.of(
                AttributeKey.stringKey("operation"), operation,
                AttributeKey.booleanKey("success"), success
            ));

        // Counter для количества операций
        String counterName = success ? "operation_success_total" : "operation_failure_total";
        meter.counterBuilder(counterName)
            .setDescription("Operation success/failure count")
            .build()
            .add(1, Attributes.of(
                AttributeKey.stringKey("operation"), operation
            ));
    }

    public void recordCacheMetrics(String cacheName, String operation, long duration, boolean hit) {
        // Cache hit/miss counter
        String hitMiss = hit ? "hit" : "miss";
        meter.counterBuilder("cache_operations_total")
            .setDescription("Cache operation counter")
            .build()
            .add(1, Attributes.of(
                AttributeKey.stringKey("cache_name"), cacheName,
                AttributeKey.stringKey("operation"), operation,
                AttributeKey.stringKey("result"), hitMiss
            ));

        // Cache operation duration
        meter.histogramBuilder("cache_operation_duration")
            .setDescription("Cache operation duration")
            .setUnit("ms")
            .build()
            .record(duration, Attributes.of(
                AttributeKey.stringKey("cache_name"), cacheName,
                AttributeKey.stringKey("operation"), operation
            ));
    }

    private double getCpuUsage() {
        // Реальная реализация получения CPU usage
        return ThreadLocalRandom.current().nextDouble(0, 100);
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
}
```

## **Logs** (**Логирование**)

### **OpenTelemetry Logging**

```java
@Service
@Slf4j
public class LoggingService {

    private final Logger otelLogger;

    public LoggingService(OpenTelemetry openTelemetry) {
        // Создание OpenTelemetry логгера
        this.otelLogger = LoggerProvider.builder()
            .addLogRecordProcessor(SimpleLogRecordProcessor.create(consoleExporter()))
            .build()
            .get("com.example.logging");
    }

    public void structuredLogging() {
        // Структурированное логирование с контекстом
        Span currentSpan = Span.current();
        if (currentSpan != null) {
            log.info("Processing request with trace_id: {}, span_id: {}",
                currentSpan.getSpanContext().getTraceId(),
                currentSpan.getSpanContext().getSpanId());
        }

        // OpenTelemetry логирование
        otelLogger.logRecordBuilder()
            .setBody("User login attempt")
            .setSeverity(Severity.INFO)
            .setAttributes(Attributes.of(
                AttributeKey.stringKey("user.id"), "user123",
                AttributeKey.stringKey("login.method"), "password",
                AttributeKey.booleanKey("login.success"), true
            ))
            .emit();
    }

    public void errorLogging(Exception exception) {
        // Логирование ошибок с контекстом
        otelLogger.logRecordBuilder()
            .setBody("Database connection failed: " + exception.getMessage())
            .setSeverity(Severity.ERROR)
            .setAttributes(Attributes.of(
                AttributeKey.stringKey("error.type"), exception.getClass().getSimpleName(),
                AttributeKey.stringKey("db.host"), "localhost",
                AttributeKey.intKey("db.port"), 5432,
                AttributeKey.stringKey("db.name"), "myapp"
            ))
            .setException(exception)
            .emit();

        log.error("Database error occurred", exception);
    }

    public void performanceLogging(long duration, String operation) {
        // Логирование производительности
        Severity severity = duration > 5000 ? Severity.WARN : Severity.INFO;

        otelLogger.logRecordBuilder()
            .setBody("Operation completed")
            .setSeverity(severity)
            .setAttributes(Attributes.of(
                AttributeKey.stringKey("operation.name"), operation,
                AttributeKey.longKey("operation.duration_ms"), duration,
                AttributeKey.stringKey("performance.category"),
                duration > 5000 ? "slow" : "normal"
            ))
            .emit();

        if (duration > 5000) {
            log.warn("Slow operation detected: {} took {}ms", operation, duration);
        }
    }

    public void businessEventLogging(String eventType, Map<String, Object> eventData) {
        // Логирование бизнес событий
        AttributesBuilder attributes = Attributes.builder()
            .put("event.type", eventType)
            .put("event.timestamp", Instant.now().toString());

        eventData.forEach((key, value) -> {
            if (value instanceof String) {
                attributes.put(AttributeKey.stringKey("event." + key), (String) value);
            } else if (value instanceof Number) {
                if (value instanceof Integer) {
                    attributes.put(AttributeKey.longKey("event." + key), ((Integer) value).longValue());
                } else if (value instanceof Double) {
                    attributes.put(AttributeKey.doubleKey("event." + key), (Double) value);
                }
            } else if (value instanceof Boolean) {
                attributes.put(AttributeKey.booleanKey("event." + key), (Boolean) value);
            }
        });

        otelLogger.logRecordBuilder()
            .setBody("Business event: " + eventType)
            .setSeverity(Severity.INFO)
            .setAttributes(attributes.build())
            .emit();
    }

    private LogRecordExporter consoleExporter() {
        return ConsoleLogRecordExporter.create();
    }
}
```

### Интеграция с существующими логгерами

```java
@Configuration
public class LoggingIntegrationConfiguration {

    @Bean
    public LoggerProvider loggerProvider() {
        return LoggerProvider.builder()
            .addLogRecordProcessor(SimpleLogRecordProcessor.create(otlpExporter()))
            .build();
    }

    @Bean
    public LogRecordExporter otlpExporter() {
        return OtlpGrpcLogRecordExporter.builder()
            .setEndpoint("http://localhost:4317")
            .build();
    }

    // MDC для передачи trace context в логи
    @Bean
    public MappedDiagnosticContextScopeDecorator mdcScopeDecorator() {
        return MappedDiagnosticContextScopeDecorator.create(
            List.of("trace_id", "span_id", "trace_flags")
        );
    }
}

// Аспект для автоматического добавления trace context в MDC
@Aspect
@Component
public class TracingLoggingAspect {

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object addTraceContextToMDC(ProceedingJoinPoint joinPoint) throws Throwable {
        Span currentSpan = Span.current();
        if (currentSpan != null) {
            SpanContext spanContext = currentSpan.getSpanContext();

            try {
                // Добавление trace context в MDC для логов
                MDC.put("trace_id", spanContext.getTraceId());
                MDC.put("span_id", spanContext.getSpanId());
                MDC.put("trace_flags", spanContext.getTraceFlags().asHex());

                return joinPoint.proceed();
            } finally {
                // Очистка MDC
                MDC.remove("trace_id");
                MDC.remove("span_id");
                MDC.remove("trace_flags");
            }
        }

        return joinPoint.proceed();
    }
}
```

## Интеграция с **Spring Boot**

### Автоматическая конфигурация

```yaml
# application.yml
otel:
  service:
    name: my-spring-boot-app
    version: 1.0.0
    namespace: production

  traces:
    exporter: jaeger
    endpoint: http://localhost:14250

  metrics:
    exporter: prometheus

  logs:
    exporter: otlp
    endpoint: http://localhost:4317

# Jaeger настройки
otel:
  exporter:
    jaeger:
      endpoint: http://localhost:14250/api/traces

# OTLP настройки
otel:
  exporter:
    otlp:
      endpoint: http://localhost:4317
      headers: api-key=secret

# Prometheus метрики
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### **Spring Boot** конфигурация

```java
@Configuration
public class OpenTelemetrySpringConfiguration {

    @Bean
    public OpenTelemetry openTelemetry() {
        Resource resource = Resource.getDefault()
            .merge(Resource.builder()
                .put("service.name", "my-spring-boot-app")
                .put("service.version", "1.0.0")
                .build());

        return OpenTelemetrySdk.builder()
            .setResource(resource)
            .setTracerProvider(tracerProvider())
            .setMeterProvider(meterProvider())
            .setLoggerProvider(loggerProvider())
            .buildAndRegisterGlobal();
    }

    @Bean
    public TracerProvider tracerProvider() {
        return TracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(jaegerExporter()).build())
            .build();
    }

    @Bean
    public MeterProvider meterProvider() {
        return MeterProvider.builder()
            .registerMetricReader(PeriodicMetricReader.builder(prometheusExporter())
                .setInterval(Duration.ofSeconds(30))
                .build())
            .build();
    }

    @Bean
    public LoggerProvider loggerProvider() {
        return LoggerProvider.builder()
            .addLogRecordProcessor(BatchLogRecordProcessor.builder(otlpExporter()).build())
            .build();
    }

    private SpanExporter jaegerExporter() {
        return JaegerGrpcSpanExporter.builder()
            .setEndpoint(System.getProperty("otel.exporter.jaeger.endpoint", "http://localhost:14250"))
            .build();
    }

    private MetricExporter prometheusExporter() {
        return PrometheusMetricExporter.builder().build();
    }

    private LogRecordExporter otlpExporter() {
        return OtlpGrpcLogRecordExporter.builder()
            .setEndpoint(System.getProperty("otel.exporter.otlp.endpoint", "http://localhost:4317"))
            .build();
    }
}
```

### Аспект для автоматического трейсинга

```java
@Aspect
@Component
public class TracingAspect {

    private final Tracer tracer;

    public TracingAspect(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("com.example.aspect");
    }

    @Around("@annotation(com.example.Traceable)")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Span span = tracer.spanBuilder(className + "." + methodName)
            .setAttribute("method.name", methodName)
            .setAttribute("class.name", className)
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Добавление параметров как атрибутов
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < args.length; i++) {
                if (args[i] != null) {
                    span.setAttribute("arg." + i, args[i].toString());
                }
            }

            Object result = joinPoint.proceed();

            // Добавление результата
            if (result != null) {
                span.setAttribute("result.type", result.getClass().getSimpleName());
            }

            return result;

        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    @Around("@within(org.springframework.stereotype.Service)")
    public Object traceServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Span span = tracer.spanBuilder("service." + className + "." + methodName)
            .setAttribute("component", "service")
            .setAttribute("method", methodName)
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            return joinPoint.proceed();
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Traceable {
    String value() default "";
}
```

### **REST** контроллеры с трейсингом

```java
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final Tracer tracer;
    private final Meter meter;

    public UserController(UserService userService, OpenTelemetry openTelemetry) {
        this.userService = userService;
        this.tracer = openTelemetry.getTracer("com.example.controller");
        this.meter = openTelemetry.getMeter("com.example.controller");
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(@RequestParam(required = false) String name) {
        Span span = tracer.spanBuilder("getUsers")
            .setAttribute("http.method", "GET")
            .setAttribute("http.url", "/api/users")
            .setAttribute("user.filter", name != null ? name : "none")
            .startSpan();

        LongCounter requestCounter = meter.counterBuilder("http_requests_total")
            .build();

        try (Scope scope = span.makeCurrent()) {
            long startTime = System.nanoTime();

            List<UserDto> users = userService.findUsers(name);

            long duration = System.nanoTime() - startTime;

            // Метрики
            requestCounter.add(1, Attributes.of(
                AttributeKey.stringKey("method"), "GET",
                AttributeKey.stringKey("endpoint"), "/api/users",
                AttributeKey.stringKey("status"), "200"
            ));

            span.setAttribute("users.count", users.size());
            span.setAttribute("response.time_ns", duration);

            return ResponseEntity.ok(users);

        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR, e.getMessage());

            // Метрики для ошибок
            requestCounter.add(1, Attributes.of(
                AttributeKey.stringKey("method"), "GET",
                AttributeKey.stringKey("endpoint"), "/api/users",
                AttributeKey.stringKey("status"), "500"
            ));

            throw e;
        } finally {
            span.end();
        }
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        Span span = tracer.spanBuilder("createUser")
            .setAttribute("http.method", "POST")
            .setAttribute("http.url", "/api/users")
            .setAttribute("user.email", request.getEmail())
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            UserDto user = userService.createUser(request);

            span.setAttribute("user.id", user.getId());
            span.setAttribute("user.created", true);

            return ResponseEntity.status(HttpStatus.CREATED).body(user);

        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

## Экспорт данных

### **Jaeger**

```java
@Configuration
public class JaegerConfiguration {

    @Bean
    public SpanExporter jaegerExporter() {
        return JaegerGrpcSpanExporter.builder()
            .setEndpoint("http://jaeger-collector:14250")
            .setServiceName("my-service")
            .build();
    }

    @Bean
    public TracerProvider tracerProvider(SpanExporter jaegerExporter) {
        return TracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(jaegerExporter)
                .setScheduleDelay(Duration.ofSeconds(5))
                .setMaxExportBatchSize(512)
                .setExportTimeout(Duration.ofSeconds(30))
                .build())
            .build();
    }
}
```

### **Prometheus**

```java
@Configuration
public class PrometheusConfiguration {

    @Bean
    public PrometheusMeterRegistry prometheusRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Bean
    public MeterProvider meterProvider(PrometheusMeterRegistry prometheusRegistry) {
        return MeterProvider.builder()
            .registerMetricReader(PeriodicMetricReader.builder(
                PrometheusMetricExporter.builder()
                    .setRegistry(prometheusRegistry.getPrometheusRegistry())
                    .build())
                .setInterval(Duration.ofSeconds(30))
                .build())
            .build();
    }

    @RestController
    public class MetricsController {

        private final PrometheusMeterRegistry prometheusRegistry;

        public MetricsController(PrometheusMeterRegistry prometheusRegistry) {
            this.prometheusRegistry = prometheusRegistry;
        }

        @GetMapping("/actuator/prometheus")
        public String prometheus() {
            return prometheusRegistry.scrape();
        }
    }
}
```

### **OTLP** (**OpenTelemetry Protocol**)

```java
@Configuration
public class OTLPConfiguration {

    @Bean
    public SpanExporter otlpSpanExporter() {
        return OtlpGrpcSpanExporter.builder()
            .setEndpoint("http://otel-collector:4317")
            .setTimeout(Duration.ofSeconds(30))
            .build();
    }

    @Bean
    public MetricExporter otlpMetricExporter() {
        return OtlpGrpcMetricExporter.builder()
            .setEndpoint("http://otel-collector:4317")
            .setTimeout(Duration.ofSeconds(30))
            .build();
    }

    @Bean
    public LogRecordExporter otlpLogExporter() {
        return OtlpGrpcLogRecordExporter.builder()
            .setEndpoint("http://otel-collector:4317")
            .setTimeout(Duration.ofSeconds(30))
            .build();
    }

    @Bean
    public TracerProvider tracerProvider(SpanExporter spanExporter) {
        return TracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
            .build();
    }

    @Bean
    public MeterProvider meterProvider(MetricExporter metricExporter) {
        return MeterProvider.builder()
            .registerMetricReader(PeriodicMetricReader.builder(metricExporter)
                .setInterval(Duration.ofSeconds(60))
                .build())
            .build();
    }

    @Bean
    public LoggerProvider loggerProvider(LogRecordExporter logExporter) {
        return LoggerProvider.builder()
            .addLogRecordProcessor(BatchLogRecordProcessor.builder(logExporter).build())
            .build();
    }
}
```

### **ELK Stack**

```java
@Configuration
public class ELKConfiguration {

    @Bean
    public LogRecordExporter elkExporter() {
        // Настройка экспорта в Elasticsearch через Logstash
        return new ElasticsearchLogRecordExporter(
            "http://logstash:5044",
            "my-service"
        );
    }

    @Bean
    public LoggerProvider loggerProvider(LogRecordExporter elkExporter) {
        return LoggerProvider.builder()
            .addLogRecordProcessor(BatchLogRecordProcessor.builder(elkExporter).build())
            .build();
    }
}
```

## **Best practices**

### 1. Правильная структура трейсов

```java
// ✅ Хорошо - логичная структура трейсов
@Service
public class StructuredTracingService {

    private final Tracer tracer;

    public StructuredTracingService(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("com.example.structured");
    }

    public OrderResult processOrder(CreateOrderRequest request) {
        Span orderSpan = tracer.spanBuilder("processOrder")
            .setAttribute("order.customer_id", request.getCustomerId())
            .setAttribute("order.total_amount", request.getTotalAmount())
            .setAttribute("order.items_count", request.getItems().size())
            .startSpan();

        try (Scope orderScope = orderSpan.makeCurrent()) {
            // Валидация заказа
            Span validationSpan = tracer.spanBuilder("validateOrder")
                .setAttribute("validation.type", "business_rules")
                .startSpan();

            try (Scope validationScope = validationSpan.makeCurrent()) {
                validateOrder(request);
                validationSpan.addEvent("Order validation completed");
            } finally {
                validationSpan.end();
            }

            // Расчет стоимости
            Span calculationSpan = tracer.spanBuilder("calculateTotal")
                .setAttribute("calculation.type", "tax_and_discount")
                .startSpan();

            BigDecimal total = BigDecimal.ZERO;
            try (Scope calcScope = calculationSpan.makeCurrent()) {
                total = calculateTotal(request);
                calculationSpan.setAttribute("calculation.result", total.doubleValue());
            } finally {
                calculationSpan.end();
            }

            // Сохранение в БД
            Span dbSpan = tracer.spanBuilder("saveOrder")
                .setAttribute("db.operation", "insert")
                .setAttribute("db.table", "orders")
                .startSpan();

            Long orderId = null;
            try (Scope dbScope = dbSpan.makeCurrent()) {
                orderId = saveOrderToDatabase(request, total);
                dbSpan.setAttribute("db.rows_affected", 1);
                dbSpan.setAttribute("order.id", orderId);
            } finally {
                dbSpan.end();
            }

            // Отправка уведомления
            Span notificationSpan = tracer.spanBuilder("sendNotification")
                .setAttribute("notification.type", "email")
                .setAttribute("notification.recipient", request.getCustomerId())
                .startSpan();

            try (Scope notifScope = notificationSpan.makeCurrent()) {
                sendOrderConfirmation(request.getCustomerId(), orderId);
                notificationSpan.addEvent("Order confirmation sent");
            } finally {
                notificationSpan.end();
            }

            orderSpan.setAttribute("order.id", orderId);
            orderSpan.setAttribute("order.final_amount", total.doubleValue());
            orderSpan.addEvent("Order processing completed successfully");

            return new OrderResult(orderId, total);

        } catch (ValidationException e) {
            orderSpan.recordException(e);
            orderSpan.setStatus(StatusCode.ERROR, "Order validation failed");
            orderSpan.addEvent("Order processing failed due to validation");
            throw e;
        } catch (Exception e) {
            orderSpan.recordException(e);
            orderSpan.setStatus(StatusCode.ERROR, "Order processing failed: " + e.getMessage());
            throw e;
        } finally {
            orderSpan.end();
        }
    }

    private void validateOrder(CreateOrderRequest request) {
        // Валидация
        if (request.getItems().isEmpty()) {
            throw new ValidationException("Order must contain at least one item");
        }
    }

    private BigDecimal calculateTotal(CreateOrderRequest request) {
        // Расчет стоимости
        return request.getItems().stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Long saveOrderToDatabase(CreateOrderRequest request, BigDecimal total) {
        // Сохранение в БД
        return 12345L; // Имитация
    }

    private void sendOrderConfirmation(String customerId, Long orderId) {
        // Отправка уведомления
    }
}

// ❌ Плохо - плоская структура трейсов
@Service
public class FlatTracingService {

    private final Tracer tracer;

    public FlatTracingService(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("com.example.flat");
    }

    public OrderResult processOrder(CreateOrderRequest request) {
        // Один большой span без структуры
        Span span = tracer.spanBuilder("processOrder")
            .setAttribute("all.operations", "validate,calculate,save,notify")
            .startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Все операции в одном span - сложно анализировать
            validateOrder(request);
            BigDecimal total = calculateTotal(request);
            Long orderId = saveOrderToDatabase(request, total);
            sendOrderConfirmation(request.getCustomerId(), orderId);

            span.setAttribute("result.order_id", orderId);
            return new OrderResult(orderId, total);
        } catch (Exception e) {
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }
}
```

### 2. Эффективные метрики

```java
// ✅ Хорошо - эффективные метрики
@Service
public class EfficientMetricsService {

    private final Meter meter;
    private final LongCounter httpRequestsTotal;
    private final DoubleHistogram httpRequestDuration;
    private final UpDownCounter activeConnections;

    public EfficientMetricsService(OpenTelemetry openTelemetry) {
        this.meter = openTelemetry.getMeter("com.example.efficient");

        // Предварительное создание метрик для производительности
        this.httpRequestsTotal = meter.counterBuilder("http_requests_total")
            .setDescription("Total HTTP requests")
            .build();

        this.httpRequestDuration = meter.histogramBuilder("http_request_duration_seconds")
            .setDescription("HTTP request duration")
            .setUnit("s")
            .build();

        this.activeConnections = meter.upDownCounterBuilder("active_connections")
            .setDescription("Number of active connections")
            .build();
    }

    public void recordHttpRequest(String method, String path, int statusCode, long durationMs) {
        // Использование предварительно созданных метрик
        Attributes attributes = Attributes.of(
            AttributeKey.stringKey("method"), method,
            AttributeKey.stringKey("path"), path,
            AttributeKey.longKey("status_code"), (long) statusCode
        );

        httpRequestsTotal.add(1, attributes);
        httpRequestDuration.record(durationMs / 1000.0, attributes);
    }

    public void updateActiveConnections(int delta) {
        activeConnections.add(delta);
    }

    // Кэширование атрибутов для повторяющихся значений
    private final Map<String, Attributes> attributesCache = new ConcurrentHashMap<>();

    public void recordCachedMetrics(String cacheKey, String operation, boolean success) {
        Attributes attributes = attributesCache.computeIfAbsent(cacheKey + operation + success,
            key -> Attributes.of(
                AttributeKey.stringKey("cache"), cacheKey,
                AttributeKey.stringKey("operation"), operation,
                AttributeKey.booleanKey("success"), success
            ));

        meter.counterBuilder("cache_operations_total")
            .build()
            .add(1, attributes);
    }

    // Batch обновления для снижения накладных расходов
    public void batchMetricsUpdate(List<MetricUpdate> updates) {
        Map<Attributes, Long> requestCounts = new HashMap<>();
        Map<Attributes, List<Double>> durations = new HashMap<>();

        // Группировка обновлений
        for (MetricUpdate update : updates) {
            Attributes attrs = Attributes.of(
                AttributeKey.stringKey("method"), update.getMethod(),
                AttributeKey.stringKey("path"), update.getPath(),
                AttributeKey.longKey("status_code"), (long) update.getStatusCode()
            );

            requestCounts.merge(attrs, 1L, Long::sum);
            durations.computeIfAbsent(attrs, k -> new ArrayList<>())
                .add(update.getDurationMs() / 1000.0);
        }

        // Batch применение
        requestCounts.forEach((attrs, count) -> httpRequestsTotal.add(count, attrs));
        durations.forEach((attrs, durationList) ->
            durationList.forEach(duration -> httpRequestDuration.record(duration, attrs)));
    }

    static class MetricUpdate {
        private final String method;
        private final String path;
        private final int statusCode;
        private final long durationMs;

        public MetricUpdate(String method, String path, int statusCode, long durationMs) {
            this.method = method;
            this.path = path;
            this.statusCode = statusCode;
            this.durationMs = durationMs;
        }

        // getters...
    }
}

// ❌ Плохо - неэффективные метрики
@Service
public class InefficientMetricsService {

    private final Meter meter;

    public InefficientMetricsService(OpenTelemetry openTelemetry) {
        this.meter = openTelemetry.getMeter("com.example.inefficient");
    }

    public void recordHttpRequest(String method, String path, int statusCode, long durationMs) {
        // Создание новой метрики при каждом вызове - неэффективно
        meter.counterBuilder("http_requests_total")
            .build()
            .add(1, Attributes.of(
                AttributeKey.stringKey("method"), method,
                AttributeKey.stringKey("path"), path,
                AttributeKey.longKey("status_code"), (long) statusCode
            ));

        // Отдельная метрика для каждого вызова
        meter.histogramBuilder("http_request_duration_seconds")
            .build()
            .record(durationMs / 1000.0, Attributes.of(
                AttributeKey.stringKey("method"), method,
                AttributeKey.stringKey("path"), path,
                AttributeKey.longKey("status_code"), (long) statusCode
            ));
    }
}
```

### 3. **Context propagation**

```java
// ✅ Хорошо - правильная propagation
@Configuration
public class ContextPropagationConfiguration {

    @Bean
    public TextMapPropagator w3cPropagator() {
        return W3CTraceContextPropagator.getInstance();
    }

    @Bean
    public TextMapPropagator baggagePropagator() {
        return W3CBaggagePropagator.getInstance();
    }

    @Bean
    public TextMapPropagator compositePropagator(
            TextMapPropagator w3cPropagator,
            TextMapPropagator baggagePropagator) {

        return TextMapPropagator.composite(w3cPropagator, baggagePropagator);
    }
}

@Service
public class PropagationService {

    private final TextMapPropagator propagator;

    public PropagationService(TextMapPropagator propagator) {
        this.propagator = propagator;
    }

    public Map<String, String> injectContext() {
        Map<String, String> headers = new HashMap<>();
        propagator.inject(Context.current(), headers,
            (carrier, key, value) -> carrier.put(key, value));
        return headers;
    }

    public Context extractContext(Map<String, String> headers) {
        return propagator.extract(Context.current(), headers,
            (carrier, key) -> carrier.get(key));
    }

    // HTTP клиент с propagation
    public String callExternalService(String url) {
        // Инъекция контекста в заголовки
        Map<String, String> headers = injectContext();

        // Добавление заголовков к HTTP запросу
        return makeHttpRequest(url, headers);
    }

    // HTTP сервер с extraction
    public String handleIncomingRequest(Map<String, String> headers, String body) {
        // Извлечение контекста из заголовков
        Context extractedContext = extractContext(headers);

        // Выполнение операции в извлеченном контексте
        try (Scope scope = extractedContext.makeCurrent()) {
            return processRequest(body);
        }
    }

    // Асинхронные операции с propagation
    public CompletableFuture<String> asyncOperation() {
        // Захват текущего контекста
        Context capturedContext = Context.current();

        return CompletableFuture.supplyAsync(() -> {
            // Восстановление контекста в новом потоке
            try (Scope scope = capturedContext.makeCurrent()) {
                return performAsyncWork();
            }
        });
    }

    // Messaging с propagation
    public void sendMessage(String queue, String message) {
        Map<String, String> headers = injectContext();
        // Добавление trace context в message headers
        messagingService.sendMessage(queue, message, headers);
    }

    public void processMessage(String message, Map<String, String> headers) {
        Context extractedContext = extractContext(headers);

        try (Scope scope = extractedContext.makeCurrent()) {
            processMessageContent(message);
        }
    }

    private String makeHttpRequest(String url, Map<String, String> headers) {
        // Имитация HTTP вызова
        return "Response from " + url;
    }

    private String processRequest(String body) {
        // Обработка запроса
        return "Processed: " + body;
    }

    private String performAsyncWork() {
        // Асинхронная работа
        return "Async result";
    }

    private void processMessageContent(String message) {
        // Обработка сообщения
    }
}

// ❌ Плохо - отсутствие propagation
@Service
public class BadPropagationService {

    public String callExternalService(String url) {
        // Контекст не передается - теряется traceability
        return makeHttpRequest(url, new HashMap<>());
    }

    public CompletableFuture<String> asyncOperation() {
        // Контекст не захватывается - асинхронные операции не трейсятся
        return CompletableFuture.supplyAsync(this::performAsyncWork);
    }

    public void sendMessage(String queue, String message) {
        // Метаданные trace не передаются
        messagingService.sendMessage(queue, message, new HashMap<>());
    }

    private String makeHttpRequest(String url, Map<String, String> headers) {
        return "Response from " + url;
    }

    private String performAsyncWork() {
        return "Async result";
    }
}
```


## Заключение

**OpenTelemetry** — это мощный и универсальный стандарт для **observability** в современных приложениях. Он предоставляет единый подход к сбору и экспорту метрик, трейсов и логов.

### Преимущества **OpenTelemetry**

1. **Vendor neutral** — Не зависит от конкретных платформ мониторинга
2. **Единый стандарт** — Общий формат для метрик, трейсов и логов
3. **Автоматическая инструментация** — Многие фреймворки поддерживаются **out-`of-the`-box**
4. **Context propagation** — Распространение контекста между сервисами
5. **Extensible** — Легко добавить кастомную инструментацию
6. **CNCF project** — Поддерживается ведущими компаниями
7. **Широкая экосистема** — Интеграции с популярными инструментами
8. **Future-proof** — Стандарт для облачных и микросервисных архитектур

### Основные паттерны использования

1. **Distributed `Tracing` паттерн** — Отслеживание запросов через микросервисы
2. **Context `Propagation` паттерн** — Передача контекста между компонентами
3. **Metrics `Collection` паттерн** — Сбор и экспорт метрик
4. **Structured `Logging` паттерн** — Коррелированные логи с трейсами
5. **Auto `Instrumentation` паттерн** — Автоматическая инструментация
6. **Custom `Instrumentation` паттерн** — Ручная инструментация для бизнес-логики

### Когда использовать **OpenTelemetry**

**Рекомендуется:**
- Микросервисная архитектура
- Распределенные системы
- **Cloud-native** приложения
- Системы с высокими требованиями к **observability**
- Проекты с **distributed tracing**
- Приложения с **complex** бизнес-метриками

**Особенно полезно:**
- В **Kubernetes** и облачных средах
- При использовании **service mesh** (**Istio, Linkerd**)
- В системах с **event-driven** архитектурой
- При интеграции с **APM** инструментами
- В проектах с **DevOps** практиками

### Сравнение с альтернативами

| Инструмент | Преимущества | Недостатки |
|------------|-------------|------------|
| **OpenTelemetry** | **Vendor neutral**, полный охват, **CNCF standard** | Более сложная настройка |
| **Jaeger** | Специализирован на **tracing**, простой | Только **tracing** |
| **Prometheus** | Отличные метрики, **query language** | Только метрики |
| **ELK Stack** | Мощные логи, **Kibana** `UI` | Сложная настройка |
| **Zipkin** | Простой **tracing** | Ограниченные возможности |

**OpenTelemetry** рекомендуется как основной выбор для **observability** в современных **distributed** системах и микросервисных архитектурах.


[⬆️ Наверх](../)

## См. также

- [[java-apache-httpclient|Apache HttpClient: Мощный HTTP клиент для Java]]
- [[java-apache-poi|Apache POI]]
- [[java-bean-validation|Bean Validation (JSR-380 / Jakarta Validation 3.0)]]
- [[java-hikaricp|HikariCP: Высокопроизводительный Connection Pool]]
- [[java-http-clients|HTTP-клиенты в Java]]
