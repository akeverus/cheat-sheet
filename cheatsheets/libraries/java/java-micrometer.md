---
title: "Micrometer: Метрики и мониторинг в Java"
description: "Комплексное руководство по использованию Micrometer — библиотеки для создания и экспорта метрик в Java приложениях с поддержкой Prometheus, InfluxDB, CloudWatch и других систем мониторинга."
tags:
  - libraries
  - java
  - java-micrometer
type: "overview"
difficulty: "intermediate"
aliases:
  - "Micrometer"
  - "Метрики и мониторинг в Java"
  - "java micrometer"
prerequisites:
  - "[[java-lombok]]"
next: []
updated: "2026-04-20"
---
# Micrometer: Метрики и мониторинг в Java

**Комплексное руководство по использованию `Micrometer` — библиотеки для создания и экспорта метрик в `Java` приложениях с поддержкой `Prometheus`, `InfluxDB`, `CloudWatch` и других систем мониторинга.**

## Полезные ссылки

### Официальная документация
- [Micrometer](https://micrometer.io/) — официальный сайт
- [Micrometer Documentation](https://micrometer.io/docs) — полная документация
- [Micrometer GitHub](https://github.com/micrometer-metrics/micrometer) — репозиторий проекта

### Интеграция
- [Spring Boot Micrometer](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics) — **Spring Boot** интеграция
- [Micrometer Registry](https://micrometer.io/docs/concepts#_registry) — реестры метрик
- [Micrometer Samples](https://github.com/micrometer-metrics/micrometer-samples) — примеры

## Содержание

- [Введение в Micrometer](#введение-в-micrometer)
  - [Почему Micrometer?](#почему-micrometer)
  - [Основные компоненты](#основные-компоненты)
  - [Типы метрик](#типы-метрик)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Базовая настройка](#базовая-настройка)
- [Основные концепции](#основные-концепции)
  - [MeterRegistry](#meterregistry)
  - [Tags и измерения](#tags-и-измерения)
- [Counters](#counters)
  - [Основы Counter](#основы-counter)
  - [Продвинутые счетчики](#продвинутые-счетчики)
- [Gauges](#gauges)
  - [Основы Gauge](#основы-gauge)
  - [Продвинутые gauges](#продвинутые-gauges)
- [Timers](#timers)
  - [Основы Timer](#основы-timer)
  - [Продвинутые таймеры](#продвинутые-таймеры)
- [Distribution Summaries](#distribution-summaries)
  - [Основы DistributionSummary](#основы-distributionsummary)
  - [Продвинутые distribution summaries](#продвинутые-distribution-summaries)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Автоматическая конфигурация](#автоматическая-конфигурация)
  - [Кастомные метрики в Spring Boot](#кастомные-метрики-в-spring-boot)
  - [Аспект для измерения времени выполнения](#аспект-для-измерения-времени-выполнения)
- [Экспорт метрик](#экспорт-метрик)
  - [Prometheus](#prometheus)
  - [InfluxDB](#influxdb)
  - [JMX](#jmx)
  - [Composite registry](#composite-registry)
- [Best practices](#best-practices)
  - [1. Именование метрик](#1-именование-метрик)
  - [2. Теги и измерения](#2-теги-и-измерения)
  - [3. Производительность и оптимизация](#3-производительность-и-оптимизация)
  - [4. Мониторинг и алертинг](#4-мониторинг-и-алертинг)
- [Заключение](#заключение)
  - [Преимущества Micrometer](#преимущества-micrometer)
  - [Основные паттерны использования](#основные-паттерны-использования)
  - [Когда использовать Micrometer](#когда-использовать-micrometer)
  - [Сравнение с альтернативами](#сравнение-с-альтернативами)
- [См. также](#см-также)

## Введение в Micrometer

**Micrometer** — это библиотека для создания и экспорта метрик в **Java** приложениях. Она предоставляет **vendor-neutral** интерфейс для интеграции с различными системами мониторинга: **Prometheus**, **InfluxDB**, **CloudWatch**, **New Relic**, **Datadog** и другими.

### Почему Micrometer?

**Micrometer** предлагает множество преимуществ:**

1. **Vendor neutral** — Единый **API** для всех систем мониторинга
2. **Dimensional metrics** — Поддержка тегов и измерений
3. **Hierarchical naming** — Иерархическая структура имен метрик
4. **Spring `Boot` интеграция** — Автоматическая конфигурация
5. **Reactive support** — Поддержка **reactive** приложений
6. **Performance optimized** — Низкие накладные расходы
7. **Extensible** — Легко добавить новые **registry**
8. **Backwards compatible** — Поддержка **legacy** систем

### Основные компоненты

- **Meter** — Интерфейс для создания метрик
- **MeterRegistry** — Реестр метрик для конкретной системы мониторинга
- **MeterBinder** — Автоматическая регистрация метрик для компонентов
- **Tags** — Измерения для категоризации метрик
- **CompositeMeterRegistry** — Комбинированный реестр для нескольких систем

### Типы метрик

- **Counter** — Монотонно возрастающий счетчик
- **Gauge** — Значение, которое может расти и падать
- **Timer** — Измерение времени выполнения операций
- **DistributionSummary** — Статистика распределения значений
- **LongTaskTimer** — Таймер для длительных задач

## Установка и настройка

### Maven

Зависимости **Maven** для **Micrometer** (core и реестры `Prometheus`, `InfluxDB`, JMX).

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
    <version>1.11.4</version>
</dependency>

<!-- Для Prometheus -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <version>1.11.4</version>
</dependency>

<!-- Для InfluxDB -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-influx</artifactId>
    <version>1.11.4</version>
</dependency>

<!-- Для JMX -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-jmx</artifactId>
    <version>1.11.4</version>
</dependency>
```

### Gradle

```kotlin
dependencies {
    implementation("io.micrometer:micrometer-core:1.11.4")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus:1.11.4")
    runtimeOnly("io.micrometer:micrometer-registry-influx:1.11.4")
}
```

### Базовая настройка

```java
@Configuration
public class MetricsConfiguration {

    @Bean
    public MeterRegistry meterRegistry() {
        // Composite registry для поддержки нескольких систем мониторинга
        CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry();

        // Prometheus registry
        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        compositeRegistry.add(prometheusRegistry);

        // JMX registry для локального мониторинга
        JmxMeterRegistry jmxRegistry = new JmxMeterRegistry(JmxConfig.DEFAULT);
        compositeRegistry.add(jmxRegistry);

        return compositeRegistry;
    }

    @Bean
    public PrometheusMeterRegistry prometheusRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }
}
```

## Основные концепции

### MeterRegistry

```java
@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void demonstrateRegistry() {
        // Получение метрики по имени
        Counter counter = meterRegistry.counter("requests.total", "method", "GET", "endpoint", "/api/users");
        counter.increment();

        // Получение gauge
        Gauge gauge = Gauge.builder("active.connections", this::getActiveConnections)
            .register(meterRegistry);

        // Получение timer
        Timer timer = meterRegistry.timer("request.duration", "method", "GET");

        // Получение всех метрик
        Iterator<Meter> meters = meterRegistry.iterator();
        while (meters.hasNext()) {
            Meter meter = meters.next();
            System.out.println("Meter: " + meter.getId());
        }
    }

    private double getActiveConnections() {
        // Имитация получения количества активных соединений
        return ThreadLocalRandom.current().nextDouble(10, 50);
    }
}
```

### Tags и измерения

```java
/
 * Сервис для демонстрации работы с тегами в Micrometer
 * Теги позволяют группировать и фильтровать метрики по различным измерениям
 */
@Service
public class TaggedMetricsService {

    // MeterRegistry - центральный реестр для всех метрик в приложении
    private final MeterRegistry meterRegistry;

    /
     * Конструктор с внедрением MeterRegistry через dependency injection
     * @param meterRegistry реестр метрик для регистрации и управления метриками
     */
    public TaggedMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /
     * Демонстрация создания метрик с тегами
     * Теги позволяют добавлять дополнительные измерения к метрикам для детального анализа
     */
    public void demonstrateTags() {
        // Создание метрики Counter с тегами через Builder API
        // Теги добавляют измерения: method=GET, uri=/api/users, status=200
        // Это позволяет анализировать метрики по HTTP методу, URI и статусу ответа
        Counter.builder("http.requests")  // Имя метрики
            .description("HTTP request count")  // Описание метрики для документации
            .tags("method", "GET", "uri", "/api/users", "status", "200")  // Пары ключ-значение для тегов
            .register(meterRegistry)  // Регистрируем метрику в реестре
            .increment();  // Увеличиваем счетчик на 1

        // Динамические теги - создание тегов программно
        // Полезно когда теги зависят от условий выполнения
        Tags dynamicTags = Tags.of(
            "method", "POST",        // HTTP метод запроса
            "uri", "/api/users",     // URI запроса
            "status", "201",         // HTTP статус ответа
            "user_type", "premium"   // Тип пользователя (динамический тег)
        );

        // Создание Counter с динамическими тегами
        meterRegistry.counter("user.registrations", dynamicTags).increment();

        // Условные теги - теги зависят от системных свойств или конфигурации
        // Получаем значение переменной окружения или используем значение по умолчанию
        String environment = System.getProperty("environment", "dev");
        Tags envTags = Tags.of("environment", environment);  // Тег окружения (dev, staging, prod)

        // Создание Gauge метрики с тегами окружения
        // Gauge показывает текущее значение (CPU usage в данном случае)
        meterRegistry.gauge("system.cpu.usage", envTags, this::getCpuUsage);
    }

    /
     * Имитация получения CPU usage
     * В реальном приложении здесь будет вызов системного API для получения CPU usage
     * @return значение CPU usage в процентах (0-100)
     */
    private double getCpuUsage() {
        // Имитация получения CPU usage
        // В реальном приложении: ManagementFactory.getOperatingSystemMXBean().getProcessCpuLoad()
        return ThreadLocalRandom.current().nextDouble(0, 100);
    }

    /
     * Демонстрация фильтрации метрик по тегам
     * Позволяет находить и анализировать конкретные метрики по их тегам
     */
    public void demonstrateTagFilters() {
        // Фильтрация метрик по тегам - поиск Counter метрик с определенными тегами
        // Находим все метрики "http.requests" с тегом method=GET
        meterRegistry.find("http.requests")  // Ищем метрику по имени
            .tags("method", "GET")            // Фильтруем по тегам
            .counter()                        // Получаем Counter метрику
            .ifPresent(counter -> {           // Если метрика найдена, выводим ее значение
                System.out.println("GET requests: " + counter.count());
            });

        // Поиск всех метрик с определенным тегом
        // Находим все метрики "http.requests" со статусом 200
        meterRegistry.find("http.requests")  // Ищем метрику по имени
            .tag("status", "200")             // Фильтруем по одному тегу
            .meters()                        // Получаем все найденные метрики (Counter, Gauge, Timer и т.д.)
            .forEach(meter -> {               // Итерируемся по найденным метрикам
                System.out.println("Meter: " + meter.getId());  // Выводим ID метрики
            });
    }
}
```

## Counters

### Основы Counter

```java
/
 * Сервис для демонстрации работы с Counter метриками в Micrometer
 * Counter - это метрика которая может только увеличиваться, используется для подсчета событий
 */
@Service
public class CounterService {

    // MeterRegistry - центральный реестр для всех метрик
    private final MeterRegistry meterRegistry;

    /
     * Конструктор с внедрением MeterRegistry через dependency injection
     * @param meterRegistry реестр метрик для регистрации Counter метрик
     */
    public CounterService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /
     * Демонстрация различных способов создания и использования Counter метрик
     */
    public void demonstrateCounters() {
        // Простой counter - самый простой способ создания Counter
        // Создается Counter с именем "requests.total" без тегов
        Counter simpleCounter = meterRegistry.counter("requests.total");
        simpleCounter.increment();  // Увеличиваем счетчик на 1

        // Counter с тегами - позволяет группировать метрики по различным измерениям
        // Теги добавляют контекст: service=user-service, version=v1
        Counter taggedCounter = Counter.builder("api.calls")  // Имя метрики
            .description("API call counter")                   // Описание для документации
            .tags("service", "user-service", "version", "v1") // Теги для группировки
            .register(meterRegistry);                          // Регистрация в реестре

        taggedCounter.increment(5);  // Увеличиваем счетчик на 5 (можно указать любое значение)

        // Counter с функцией - автоматическое обновление на основе внешнего состояния
        // AtomicLong используется как источник данных для Counter
        AtomicLong requestCount = new AtomicLong(0);  // Счетчик запросов
        Counter functionalCounter = Counter.builder("functional.requests")
            .register(meterRegistry, requestCount::incrementAndGet);  // Функция для автоматического обновления

        // Автоматическое увеличение - при вызове increment() вызывается функция
        // Это позволяет синхронизировать Counter с внешним состоянием
        functionalCounter.increment();
    }

    /
     * Пример использования Counter в бизнес-логике
     * Демонстрирует как отслеживать различные события в приложении
     */
    public void businessLogicWithCounters() {
        // Создаем Counter метрики для различных событий
        Counter userRegistrations = meterRegistry.counter("user.registrations");  // Регистрации пользователей
        Counter loginAttempts = meterRegistry.counter("login.attempts");          // Попытки входа
        Counter loginFailures = meterRegistry.counter("login.failures");          // Неудачные попытки входа

        // Имитация бизнес логики - выполнение входа пользователя
        boolean loginSuccess = performLogin();

        // Увеличиваем счетчик регистраций (в реальном приложении это будет в методе регистрации)
        userRegistrations.increment();

        // Условное увеличение счетчика - увеличиваем только при неудачной попытке входа
        if (!loginSuccess) {
            loginFailures.increment();  // Увеличиваем счетчик неудачных попыток
        }

        // Всегда увеличиваем счетчик попыток входа (независимо от результата)
        loginAttempts.increment();
    }

    /
     * Имитация логики входа пользователя
     * В реальном приложении здесь будет проверка учетных данных
     * @return true если вход успешен, false в противном случае
     */
    private boolean performLogin() {
        // Имитация логики входа - случайный результат для демонстрации
        // В реальном приложении: проверка username/password, валидация токена и т.д.
        return ThreadLocalRandom.current().nextBoolean();
    }
}
```

### Продвинутые счетчики

```java
@Service
@Slf4j
public class AdvancedCounterService {

    private final MeterRegistry meterRegistry;

    public AdvancedCounterService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Счетчик для HTTP запросов
    public void recordHttpRequest(String method, String uri, int statusCode, long durationMs) {
        // Общий счетчик запросов
        meterRegistry.counter("http.requests.total",
            Tags.of("method", method, "uri", uri, "status", String.valueOf(statusCode)))
            .increment();

        // Счетчик по статусам
        if (statusCode >= 200 && statusCode < 300) {
            meterRegistry.counter("http.requests.success").increment();
        } else if (statusCode >= 400 && statusCode < 500) {
            meterRegistry.counter("http.requests.client_error").increment();
        } else if (statusCode >= 500) {
            meterRegistry.counter("http.requests.server_error").increment();
        }

        // Счетчик медленных запросов
        if (durationMs > 1000) {
            meterRegistry.counter("http.requests.slow").increment();
        }
    }

    // Счетчик для бизнес метрик
    public void recordBusinessMetrics(String operation, boolean success) {
        String baseName = "business." + operation;

        meterRegistry.counter(baseName + ".attempts").increment();

        if (success) {
            meterRegistry.counter(baseName + ".success").increment();
        } else {
            meterRegistry.counter(baseName + ".failure").increment();
        }
    }

    // Пакетное обновление счетчиков
    public void batchUpdateCounters(Map<String, Long> counterUpdates) {
        counterUpdates.forEach((counterName, increment) -> {
            meterRegistry.counter(counterName).increment(increment);
        });
    }

    // Счетчик с автоматическим сбросом (экспериментальная функция)
    public void demonstrateRateCounter() {
        // Использование FunctionCounter для rate-based метрик
        AtomicLong eventCount = new AtomicLong(0);

        FunctionCounter.builder("events.per.second", eventCount, AtomicLong::get)
            .description("Events processed per second")
            .register(meterRegistry);

        // Имитация обработки событий
        for (int i = 0; i < 100; i++) {
            eventCount.incrementAndGet();
            try {
                Thread.sleep(10); // Имитация работы
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

## Gauges

### Основы Gauge

```java
@Service
public class GaugeService {

    private final MeterRegistry meterRegistry;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    public GaugeService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        // Регистрация gauge для активных соединений
        Gauge.builder("db.connections.active", activeConnections, AtomicInteger::get)
            .description("Active database connections")
            .register(meterRegistry);
    }

    public void demonstrateGauges() {
        // Gauge для коллекции
        List<String> activeUsers = new ArrayList<>();
        Gauge.builder("users.active", activeUsers, List::size)
            .description("Number of active users")
            .register(meterRegistry);

        // Имитация изменения размера коллекции
        activeUsers.add("user1");
        activeUsers.add("user2");
        activeUsers.remove("user1");

        // Gauge для объекта
        SystemMetrics systemMetrics = new SystemMetrics();
        Gauge.builder("system.memory.usage", systemMetrics, SystemMetrics::getMemoryUsage)
            .description("System memory usage percentage")
            .register(meterRegistry);

        // Gauge с тегами
        Gauge.builder("queue.size", this::getQueueSize)
            .description("Queue size")
            .tags("queue", "processing", "type", "messages")
            .register(meterRegistry);
    }

    public void connectionLifecycle() {
        // Имитация жизненного цикла соединений
        activeConnections.incrementAndGet(); // Новое соединение
        // ... использование соединения ...
        activeConnections.decrementAndGet(); // Закрытие соединения
    }

    private int getQueueSize() {
        // Имитация получения размера очереди
        return ThreadLocalRandom.current().nextInt(0, 100);
    }
}

class SystemMetrics {
    public double getMemoryUsage() {
        // Имитация получения использования памяти
        return ThreadLocalRandom.current().nextDouble(0, 100);
    }
}
```

### Продвинутые gauges

```java
@Service
@Slf4j
public class AdvancedGaugeService {

    private final MeterRegistry meterRegistry;
    private final Map<String, AtomicInteger> cacheSizes = new ConcurrentHashMap<>();

    public AdvancedGaugeService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void setupCacheMetrics(String cacheName) {
        cacheSizes.putIfAbsent(cacheName, new AtomicInteger(0));

        // Gauge для размера кэша
        Gauge.builder("cache.size", cacheSizes.get(cacheName), AtomicInteger::get)
            .description("Cache size")
            .tags("cache", cacheName)
            .register(meterRegistry);

        // Gauge для hit rate (если есть метрики кэша)
        Gauge.builder("cache.hit.ratio", this, gauge -> getCacheHitRatio(cacheName))
            .description("Cache hit ratio")
            .tags("cache", cacheName)
            .register(meterRegistry);
    }

    public void updateCacheSize(String cacheName, int newSize) {
        cacheSizes.get(cacheName).set(newSize);
    }

    // Gauge для системных ресурсов
    @PostConstruct
    public void setupSystemMetrics() {
        // CPU usage
        Gauge.builder("system.cpu.usage", this::getCpuUsage)
            .description("CPU usage percentage")
            .register(meterRegistry);

        // Memory usage
        Gauge.builder("system.memory.usage", this::getMemoryUsage)
            .description("Memory usage percentage")
            .register(meterRegistry);

        // Disk usage
        Gauge.builder("system.disk.usage", this::getDiskUsage)
            .description("Disk usage percentage")
            .register(meterRegistry);

        // Thread count
        Gauge.builder("jvm.threads.active", this::getActiveThreadCount)
            .description("Active thread count")
            .register(meterRegistry);
    }

    // Gauge для бизнес метрик
    public void setupBusinessMetrics() {
        Gauge.builder("business.active.sessions", this::getActiveSessionCount)
            .description("Active user sessions")
            .register(meterRegistry);

        Gauge.builder("business.pending.orders", this::getPendingOrderCount)
            .description("Pending orders")
            .register(meterRegistry);

        Gauge.builder("business.inventory.level", this::getInventoryLevel)
            .description("Current inventory level")
            .register(meterRegistry);
    }

    private double getCpuUsage() {
        // Реальная реализация получения CPU usage
        java.lang.management.OperatingSystemMXBean osBean =
            java.lang.management.ManagementFactory.getOperatingSystemMXBean();

        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean =
                (com.sun.management.OperatingSystemMXBean) osBean;
            return sunOsBean.getSystemCpuLoad() * 100;
        }

        return 0.0;
    }

    private double getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        return ((double) usedMemory / totalMemory) * 100;
    }

    private double getDiskUsage() {
        // Имитация получения disk usage
        return ThreadLocalRandom.current().nextDouble(10, 90);
    }

    private double getActiveThreadCount() {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        while (rootGroup.getParent() != null) {
            rootGroup = rootGroup.getParent();
        }

        int activeCount = rootGroup.activeCount();
        return activeCount;
    }

    private double getCacheHitRatio(String cacheName) {
        // Имитация получения cache hit ratio
        return ThreadLocalRandom.current().nextDouble(0.8, 0.99);
    }

    private double getActiveSessionCount() {
        // Имитация получения активных сессий
        return ThreadLocalRandom.current().nextDouble(100, 1000);
    }

    private double getPendingOrderCount() {
        // Имитация получения ожидающих заказов
        return ThreadLocalRandom.current().nextDouble(10, 100);
    }

    private double getInventoryLevel() {
        // Имитация получения уровня инвентаря
        return ThreadLocalRandom.current().nextDouble(1000, 10000);
    }
}
```

## Timers

### Основы Timer

```java
@Service
public class TimerService {

    private final MeterRegistry meterRegistry;

    public TimerService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void demonstrateTimers() {
        // Простой timer
        Timer simpleTimer = meterRegistry.timer("operation.duration");

        // Измерение времени выполнения
        simpleTimer.record(() -> {
            // Имитация работы
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Timer с тегами
        Timer taggedTimer = Timer.builder("http.request.duration")
            .description("HTTP request duration")
            .tags("method", "GET", "endpoint", "/api/users")
            .register(meterRegistry);

        // Измерение с возвратом результата
        String result = taggedTimer.recordCallable(() -> {
            Thread.sleep(50);
            return "Success";
        });

        // Ручное измерение
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            performOperation();
        } finally {
            sample.stop(Timer.builder("manual.operation").register(meterRegistry));
        }
    }

    public void httpRequestTimer(String method, String uri, Runnable operation) {
        Timer timer = Timer.builder("http.requests")
            .description("HTTP request duration")
            .tags("method", method, "uri", uri)
            .register(meterRegistry);

        timer.record(operation);
    }

    public <T> T databaseOperationTimer(String operation, Supplier<T> supplier) {
        Timer timer = Timer.builder("db.operations")
            .description("Database operation duration")
            .tags("operation", operation)
            .register(meterRegistry);

        return timer.recordCallable(supplier::get);
    }

    private void performOperation() {
        // Имитация операции
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(10, 200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### Продвинутые таймеры

```java
@Service
@Slf4j
public class AdvancedTimerService {

    private final MeterRegistry meterRegistry;

    public AdvancedTimerService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Таймер для HTTP запросов с дополнительными метриками
    public void recordHttpRequest(String method, String uri, int statusCode, long durationMs) {
        Timer timer = Timer.builder("http.request.duration")
            .description("HTTP request duration")
            .tags("method", method, "uri", uri, "status", String.valueOf(statusCode))
            .register(meterRegistry);

        timer.record(durationMs, TimeUnit.MILLISECONDS);

        // Дополнительные метрики
        if (durationMs > 1000) {
            meterRegistry.counter("http.requests.slow",
                Tags.of("method", method, "uri", uri)).increment();
        }

        if (statusCode >= 500) {
            meterRegistry.counter("http.requests.error",
                Tags.of("method", method, "status", String.valueOf(statusCode))).increment();
        }
    }

    // Таймер для бизнес операций
    public <T> T recordBusinessOperation(String operationName, Supplier<T> operation) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            T result = operation.get();

            // Успешная операция
            sample.stop(Timer.builder("business.operations")
                .description("Business operation duration")
                .tags("operation", operationName, "result", "success")
                .register(meterRegistry));

            return result;

        } catch (Exception e) {
            // Неудачная операция
            sample.stop(Timer.builder("business.operations")
                .description("Business operation duration")
                .tags("operation", operationName, "result", "error", "error_type", e.getClass().getSimpleName())
                .register(meterRegistry));

            meterRegistry.counter("business.operations.errors",
                Tags.of("operation", operationName, "error", e.getClass().getSimpleName()))
                .increment();

            throw e;
        }
    }

    // Таймер с percentile конфигурацией
    public Timer createPercentileTimer(String name, String... tags) {
        return Timer.builder(name)
            .description("Timer with percentile configuration")
            .tags(tags)
            .publishPercentiles(0.5, 0.95, 0.99) // 50%, 95%, 99% перцентили
            .publishPercentileHistogram() // Гистограмма перцентилей
            .minimumExpectedValue(Duration.ofMillis(1))
            .maximumExpectedValue(Duration.ofSeconds(30))
            .register(meterRegistry);
    }

    // Асинхронные операции
    public CompletionStage<String> asyncOperationWithTimer() {
        Timer.Sample sample = Timer.start(meterRegistry);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Имитация асинхронной работы
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 1000));
                return "Async result";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }).whenComplete((result, throwable) -> {
            if (throwable != null) {
                sample.stop(Timer.builder("async.operations")
                    .tags("result", "error")
                    .register(meterRegistry));
            } else {
                sample.stop(Timer.builder("async.operations")
                    .tags("result", "success")
                    .register(meterRegistry));
            }
        });
    }

    // Long task timer для длительных операций
    public void demonstrateLongTaskTimer() {
        LongTaskTimer longTaskTimer = LongTaskTimer.builder("batch.processing")
            .description("Batch processing duration")
            .register(meterRegistry);

        LongTaskTimer.Sample sample = longTaskTimer.start();

        try {
            // Имитация длительной пакетной обработки
            for (int i = 0; i < 100; i++) {
                processBatchItem(i);
                Thread.sleep(10); // Имитация работы
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            sample.stop();
        }
    }

    private void processBatchItem(int itemId) {
        // Имитация обработки элемента
        meterRegistry.counter("batch.items.processed").increment();
    }
}
```

## Distribution Summaries

### Основы DistributionSummary

```java
@Service
public class DistributionSummaryService {

    private final MeterRegistry meterRegistry;

    public DistributionSummaryService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void demonstrateDistributionSummaries() {
        // Distribution summary для размеров запросов
        DistributionSummary requestSizeSummary = DistributionSummary.builder("http.request.size")
            .description("HTTP request size in bytes")
            .baseUnit("bytes")
            .tags("method", "POST")
            .register(meterRegistry);

        // Запись значений
        requestSizeSummary.record(1024);  // 1KB
        requestSizeSummary.record(2048);  // 2KB
        requestSizeSummary.record(512);   // 512B

        // Distribution summary для задержек
        DistributionSummary latencySummary = DistributionSummary.builder("service.latency")
            .description("Service latency in milliseconds")
            .baseUnit("ms")
            .minimumExpectedValue(1.0)
            .maximumExpectedValue(10000.0)
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);

        // Имитация измерения задержек
        for (int i = 0; i < 100; i++) {
            long latency = ThreadLocalRandom.current().nextLong(10, 2000);
            latencySummary.record(latency);
        }
    }

    public void recordFileUploadSize(String fileName, long fileSize) {
        DistributionSummary uploadSizeSummary = DistributionSummary.builder("file.upload.size")
            .description("Uploaded file size")
            .baseUnit("bytes")
            .tags("file_type", getFileExtension(fileName))
            .register(meterRegistry);

        uploadSizeSummary.record(fileSize);
    }

    public void recordOrderAmount(BigDecimal amount) {
        DistributionSummary orderAmountSummary = DistributionSummary.builder("order.amount")
            .description("Order amount distribution")
            .baseUnit("currency")
            .tags("currency", "USD")
            .register(meterRegistry);

        orderAmountSummary.record(amount.doubleValue());
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "unknown";
    }
}
```

### Продвинутые distribution summaries

```java
@Service
public class AdvancedDistributionSummaryService {

    private final MeterRegistry meterRegistry;

    public AdvancedDistributionSummaryService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Distribution summary для ответа API
    public void recordApiResponseSize(String endpoint, String method, long responseSize) {
        DistributionSummary.builder("api.response.size")
            .description("API response size distribution")
            .baseUnit("bytes")
            .tags("endpoint", endpoint, "method", method)
            .minimumExpectedValue(100.0)     // Минимум 100 байт
            .maximumExpectedValue(1000000.0) // Максимум 1MB
            .publishPercentiles(0.5, 0.95, 0.99)
            .publishPercentileHistogram()
            .register(meterRegistry)
            .record(responseSize);
    }

    // Distribution summary для размера пакетов
    public void recordBatchSize(String operation, int batchSize) {
        DistributionSummary.builder("batch.size")
            .description("Batch processing size")
            .baseUnit("items")
            .tags("operation", operation)
            .minimumExpectedValue(1.0)
            .maximumExpectedValue(1000.0)
            .register(meterRegistry)
            .record(batchSize);
    }

    // Distribution summary для использования памяти
    public void recordMemoryUsage(String component, long usedMemory) {
        DistributionSummary.builder("memory.usage")
            .description("Memory usage by component")
            .baseUnit("bytes")
            .tags("component", component)
            .register(meterRegistry)
            .record(usedMemory);
    }

    // Кастомные buckets для гистограмм
    public DistributionSummary createCustomBucketsSummary(String name) {
        return DistributionSummary.builder(name)
            .description("Summary with custom buckets")
            .serviceLevelObjectives(
                Duration.ofMillis(100),   // 100ms
                Duration.ofMillis(500),   // 500ms
                Duration.ofSeconds(1),    // 1s
                Duration.ofSeconds(5),    // 5s
                Duration.ofSeconds(10)    // 10s
            )
            .register(meterRegistry);
    }

    // Функциональный distribution summary
    public void setupFunctionalDistributionSummary() {
        AtomicLong messageSize = new AtomicLong(0);

        // Функциональный distribution summary
        FunctionCounter.builder("messages.processed", messageSize, AtomicLong::get)
            .register(meterRegistry);

        // Имитация обработки сообщений
        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                long size = ThreadLocalRandom.current().nextLong(100, 10000);
                recordMessageSize(size);
                messageSize.incrementAndGet();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    private void recordMessageSize(long size) {
        DistributionSummary.builder("message.size")
            .description("Message size distribution")
            .baseUnit("bytes")
            .register(meterRegistry)
            .record(size);
    }
}
```

## Интеграция с Spring Boot

### Автоматическая конфигурация

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      environment: ${app.environment:dev}

# Кастомные метрики
app:
  metrics:
    enabled: true
    tags:
      service: user-service
      version: ${app.version:1.0.0}
```

### Кастомные метрики в Spring Boot

```java
@Configuration
public class CustomMetricsConfiguration {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCustomizer() {
        return registry -> {
            // Глобальные теги для всех метрик
            registry.config()
                .commonTags("application", "my-app", "region", "us-east-1");

            // Кастомные конвертеры
            registry.config()
                .meterFilter(MeterFilter.denyNameStartsWith("jvm.gc"));
        };
    }

    @Bean
    public MeterBinder customMeterBinder() {
        return registry -> {
            // Кастомные системные метрики
            Gauge.builder("system.uptime", () -> {
                return System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime();
            })
            .description("Application uptime in milliseconds")
            .register(registry);
        };
    }
}

@Service
@Slf4j
public class SpringBootMetricsService {

    private final MeterRegistry meterRegistry;

    @Autowired
    public SpringBootMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Аспект для автоматического измерения методов
    @Around("@annotation(com.example.Timed)")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Object result = joinPoint.proceed();

            sample.stop(Timer.builder("method.execution.time")
                .description("Method execution time")
                .tags("class", className, "method", methodName, "result", "success")
                .register(meterRegistry));

            return result;

        } catch (Throwable throwable) {
            sample.stop(Timer.builder("method.execution.time")
                .description("Method execution time")
                .tags("class", className, "method", methodName, "result", "error")
                .register(meterRegistry));

            meterRegistry.counter("method.execution.errors",
                Tags.of("class", className, "method", methodName, "error", throwable.getClass().getSimpleName()))
                .increment();

            throw throwable;
        }
    }

    // Метрики для REST контроллеров
    @RestController
    @RequestMapping("/api/users")
    public class UserController {

        private final MeterRegistry meterRegistry;

        public UserController(MeterRegistry meterRegistry) {
            this.meterRegistry = meterRegistry;
        }

        @GetMapping
        public List<User> getUsers() {
            return meterRegistry.timer("controller.users.get")
                .recordCallable(() -> {
                    // Имитация получения пользователей
                    meterRegistry.counter("users.fetched").increment();
                    return Arrays.asList(new User("John"), new User("Jane"));
                });
        }

        @PostMapping
        public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
            return meterRegistry.timer("controller.users.create")
                .recordCallable(() -> {
                    // Валидация
                    if (request.getName() == null || request.getName().trim().isEmpty()) {
                        meterRegistry.counter("users.validation.errors",
                            Tags.of("field", "name")).increment();
                        return ResponseEntity.badRequest().build();
                    }

                    // Создание пользователя
                    User user = new User(request.getName(), request.getEmail());
                    meterRegistry.counter("users.created").increment();

                    return ResponseEntity.ok(user);
                });
        }
    }
}
```

### Аспект для измерения времени выполнения

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timed {
    String value() default "";
    String[] extraTags() default {};
}

@Aspect
@Component
@Slf4j
public class TimedAspect {

    private final MeterRegistry meterRegistry;

    @Autowired
    public TimedAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("@annotation(timed)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, Timed timed) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Базовые теги
        ImmutableTags.Builder tagsBuilder = ImmutableTags.builder()
            .add("class", className)
            .add("method", methodName);

        // Дополнительные теги из аннотации
        for (String extraTag : timed.extraTags()) {
            String[] parts = extraTag.split("=");
            if (parts.length == 2) {
                tagsBuilder.add(parts[0], parts[1]);
            }
        }

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Object result = joinPoint.proceed();

            sample.stop(Timer.builder("method.timed")
                .description("Timed method execution")
                .tags(tagsBuilder.build())
                .register(meterRegistry));

            return result;

        } catch (Throwable throwable) {
            sample.stop(Timer.builder("method.timed.error")
                .description("Timed method execution with error")
                .tags(tagsBuilder.add("error", throwable.getClass().getSimpleName()).build())
                .register(meterRegistry));

            throw throwable;
        }
    }
}

// Использование аспекта
@Service
public class BusinessService {

    @Timed(extraTags = {"operation=calculation", "type=complex"})
    public BigDecimal performComplexCalculation(BigDecimal input) {
        // Имитация сложных вычислений
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return input.multiply(BigDecimal.valueOf(2));
    }

    @Timed("data.processing")
    public void processData(List<String> data) {
        data.forEach(item -> {
            // Обработка каждого элемента
            meterRegistry.counter("data.items.processed").increment();
        });
    }
}
```

## Экспорт метрик

### Prometheus

```java
@Configuration
public class PrometheusConfiguration {

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry(PrometheusConfig config) {
        return new PrometheusMeterRegistry(config);
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

// Пример вывода Prometheus метрик:
// # HELP http_requests_total Total HTTP requests
// # TYPE http_requests_total counter
// http_requests_total{method="GET",uri="/api/users",status="200"} 42
//
// # HELP jvm_memory_used_bytes Used bytes of a given JVM memory area.
// # TYPE jvm_memory_used_bytes gauge
// jvm_memory_used_bytes{area="heap",id="PS Eden Space"} 1.258624E7
//
// # HELP http_request_duration_seconds HTTP request duration
// # TYPE http_request_duration_seconds summary
// http_request_duration_seconds{method="GET",uri="/api/users",quantile="0.5"} 0.1
// http_request_duration_seconds{method="GET",uri="/api/users",quantile="0.95"} 0.5
// http_request_duration_seconds{method="GET",uri="/api/users",count="42",sum="15.0"}
```

### InfluxDB

```java
@Configuration
public class InfluxConfiguration {

    @Bean
    public InfluxMeterRegistry influxMeterRegistry() {
        InfluxConfig config = InfluxConfig.builder()
            .db("metrics")
            .host("localhost")
            .port(8086)
            .userName("user")
            .password("password")
            .build();

        return new InfluxMeterRegistry(config, Clock.SYSTEM);
    }
}

// Пример метрик InfluxDB:
// http_requests_total,method=GET,uri=/api/users,status=200 value=42
// jvm_memory_used_bytes,area=heap,id=PS\ Eden\ Space value=12586240
// http_request_duration_seconds_mean,method=GET,uri=/api/users value=0.357
```

### JMX

```java
@Configuration
public class JmxConfiguration {

    @Bean
    public JmxMeterRegistry jmxMeterRegistry() {
        JmxConfig config = JmxConfig.builder()
            .domain("metrics")
            .build();

        return new JmxMeterRegistry(config, Clock.SYSTEM);
    }
}
```

### Composite registry

```java
@Configuration
public class CompositeRegistryConfiguration {

    @Bean
    public MeterRegistry meterRegistry() {
        CompositeMeterRegistry composite = new CompositeMeterRegistry();

        // Prometheus для production monitoring
        PrometheusMeterRegistry prometheus = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        composite.add(prometheus);

        // InfluxDB для исторических данных
        InfluxMeterRegistry influx = new InfluxMeterRegistry(
            InfluxConfig.builder()
                .db("metrics")
                .host("influx.example.com")
                .build(),
            Clock.SYSTEM
        );
        composite.add(influx);

        // JMX для локального мониторинга
        JmxMeterRegistry jmx = new JmxMeterRegistry(JmxConfig.DEFAULT);
        composite.add(jmx);

        // CloudWatch для AWS
        CloudWatchMeterRegistry cloudWatch = new CloudWatchMeterRegistry(
            CloudWatchConfig.builder()
                .namespace("MyApp")
                .build(),
            Clock.SYSTEM,
            CloudWatchAsyncClient.create()
        );
        composite.add(cloudWatch);

        return composite;
    }

    // Отдельный bean для Prometheus endpoint
    @Bean
    public PrometheusMeterRegistry prometheusRegistry(CompositeMeterRegistry composite) {
        return composite.getRegistries().stream()
            .filter(PrometheusMeterRegistry.class::isInstance)
            .map(PrometheusMeterRegistry.class::cast)
            .findFirst()
            .orElseThrow();
    }
}
```

## Best practices

### 1. Именование метрик

```java
// ✅ Хорошо - последовательное именование
public class NamingBestPractices {

    public void demonstrateGoodNaming(MeterRegistry registry) {
        // Counter для бизнес операций
        registry.counter("orders.created").increment();
        registry.counter("users.registered").increment();

        // Timer для производительности
        registry.timer("order.processing.duration").record(() -> processOrder());

        // Gauge для состояний
        registry.gauge("inventory.level", this::getInventoryLevel);

        // Distribution summary для размеров
        registry.summary("request.payload.size").record(1024);

        // Иерархическое именование
        registry.counter("api.requests.total").increment();
        registry.counter("api.requests.success").increment();
        registry.counter("api.requests.error").increment();

        // С тегами вместо отдельных метрик
        registry.counter("api.requests", Tags.of("status", "success")).increment();
        registry.counter("api.requests", Tags.of("status", "error")).increment();
    }

    // ❌ Плохо - непоследовательное именование
    public void demonstrateBadNaming(MeterRegistry registry) {
        // Смешивание стилей
        registry.counter("OrdersCreated").increment();       // PascalCase
        registry.counter("users_registered").increment();    // snake_case
        registry.counter("OrderProcessingTime").increment(); // Неправильный тип

        // Слишком длинные имена
        registry.counter("very_long_metric_name_that_is_hard_to_read_and_use").increment();

        // Непонятные сокращения
        registry.counter("usr_reg_cnt").increment(); // usr_reg_cnt вместо users.registered

        // Метрики без описания
        registry.counter("mysterious_counter").increment();
    }

    private void processOrder() {
        // Имитация обработки заказа
    }

    private double getInventoryLevel() {
        return 150.0;
    }
}
```

### 2. Теги и измерения

```java
// ✅ Хорошо - правильное использование тегов
@Service
public class TaggingBestPractices {

    private final MeterRegistry meterRegistry;

    public TaggingBestPractices(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void demonstrateGoodTagging() {
        // HTTP метрики
        recordHttpRequest("GET", "/api/users", 200, 150);
        recordHttpRequest("POST", "/api/users", 201, 200);
        recordHttpRequest("GET", "/api/users/123", 404, 50);

        // Бизнес метрики
        recordOrder("electronics", "credit_card", 299.99);
        recordOrder("books", "paypal", 45.50);

        // Системные метрики
        recordCacheOperation("user_cache", "hit", 10);
        recordCacheOperation("product_cache", "miss", 25);
    }

    private void recordHttpRequest(String method, String uri, int status, long duration) {
        // Общий counter
        meterRegistry.counter("http.requests.total",
            Tags.of("method", method, "uri", uri, "status", String.valueOf(status)))
            .increment();

        // Timer для duration
        Timer.builder("http.requests.duration")
            .tags("method", method, "uri", uri, "status", String.valueOf(status))
            .register(meterRegistry)
            .record(duration, TimeUnit.MILLISECONDS);
    }

    private void recordOrder(String category, String paymentMethod, double amount) {
        meterRegistry.counter("orders.created",
            Tags.of("category", category, "payment_method", paymentMethod))
            .increment();

        DistributionSummary.builder("orders.amount")
            .tags("category", category, "payment_method", paymentMethod)
            .register(meterRegistry)
            .record(amount);
    }

    private void recordCacheOperation(String cacheName, String operation, long duration) {
        meterRegistry.counter("cache.operations",
            Tags.of("cache", cacheName, "operation", operation))
            .increment();

        Timer.builder("cache.operation.duration")
            .tags("cache", cacheName, "operation", operation)
            .register(meterRegistry)
            .record(duration, TimeUnit.MILLISECONDS);
    }

    // ❌ Плохо - неправильное использование тегов
    public void demonstrateBadTagging() {
        // Слишком много тегов (cardinality explosion)
        for (int i = 0; i < 1000; i++) {
            meterRegistry.counter("requests",
                Tags.of("user_id", String.valueOf(i), "timestamp", String.valueOf(System.currentTimeMillis())))
                .increment();
        }

        // Высокая кардинальность
        meterRegistry.counter("user.actions",
            Tags.of("user_email", "user" + System.currentTimeMillis() + "@example.com"))
            .increment();

        // Теги вместо отдельных метрик
        meterRegistry.counter("errors", Tags.of("type", "validation")).increment();
        meterRegistry.counter("errors", Tags.of("type", "system")).increment();
        meterRegistry.counter("errors", Tags.of("type", "network")).increment();
        // Лучше: отдельные метрики errors.validation, errors.system, errors.network
    }
}
```

### 3. Производительность и оптимизация

```java
// ✅ Хорошо - оптимизация метрик
@Configuration
public class PerformanceBestPractices {

    @Bean
    public MeterRegistry optimizedMeterRegistry() {
        CompositeMeterRegistry composite = new CompositeMeterRegistry();

        // Prometheus для production
        PrometheusMeterRegistry prometheus = new PrometheusMeterRegistry(
            PrometheusConfig.DEFAULT,
            Clock.SYSTEM
        );

        // Оптимизации Prometheus
        prometheus.config()
            .meterFilter(MeterFilter.denyNameStartsWith("jvm.")) // Исключаем JVM метрики
            .meterFilter(MeterFilter.denyNameStartsWith("tomcat.")) // Исключаем Tomcat метрики
            .meterFilter(MeterFilter.maximumAllowableTags("uri", 100, MeterFilter.deny())) // Ограничение cardinality
            .meterFilter(MeterFilter.maximumAllowableMetrics(1000, MeterFilter.deny())); // Ограничение количества метрик

        composite.add(prometheus);

        // JMX для development
        if (isDevelopmentEnvironment()) {
            JmxMeterRegistry jmx = new JmxMeterRegistry(JmxConfig.DEFAULT);
            composite.add(jmx);
        }

        return composite;
    }

    private boolean isDevelopmentEnvironment() {
        return "dev".equals(System.getProperty("environment"));
    }
}

@Service
@Slf4j
public class OptimizedMetricsService {

    private final MeterRegistry meterRegistry;

    // Кэширование meter для производительности
    private final Map<String, Counter> counterCache = new ConcurrentHashMap<>();
    private final Map<String, Timer> timerCache = new ConcurrentHashMap<>();

    public OptimizedMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementCounter(String name, String... tags) {
        String key = name + Arrays.toString(tags);
        Counter counter = counterCache.computeIfAbsent(key, k ->
            Counter.builder(name)
                .description("Cached counter")
                .tags(tags)
                .register(meterRegistry)
        );
        counter.increment();
    }

    public void recordTimer(String name, Runnable operation, String... tags) {
        String key = name + Arrays.toString(tags);
        Timer timer = timerCache.computeIfAbsent(key, k ->
            Timer.builder(name)
                .description("Cached timer")
                .tags(tags)
                .register(meterRegistry)
        );
        timer.record(operation);
    }

    // Batch обновления для снижения накладных расходов
    public void batchMetricsUpdate() {
        // Группировка обновлений для снижения количества вызовов registry
        Map<String, Long> counterUpdates = new HashMap<>();
        Map<String, List<Long>> timerUpdates = new HashMap<>();

        // Имитация накопления обновлений
        counterUpdates.put("requests.total", 10L);
        counterUpdates.put("errors.total", 2L);

        // Batch применение
        counterUpdates.forEach((name, count) ->
            meterRegistry.counter(name).increment(count));

        // Аналогично для других типов метрик
    }

    // Условное логирование метрик для снижения нагрузки
    public void conditionalMetrics() {
        // Логируем только ошибки в production
        if (isProductionEnvironment() && hasErrors()) {
            meterRegistry.counter("application.errors").increment();
        }

        // Сэмплирование для высоконагруженных операций
        if (ThreadLocalRandom.current().nextInt(100) < 10) { // 10% сэмплирование
            meterRegistry.timer("sampled.operation").record(() -> {
                // Высоконагруженная операция
            });
        }
    }

    private boolean isProductionEnvironment() {
        return "prod".equals(System.getProperty("environment"));
    }

    private boolean hasErrors() {
        // Проверка наличия ошибок
        return false;
    }
}
```

### 4. Мониторинг и алертинг

```java
// ✅ Хорошо - интеграция с системами мониторинга
@Configuration
public class MonitoringBestPractices {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> monitoringCustomizer() {
        return registry -> {
            // Глобальные теги для идентификации приложения
            registry.config()
                .commonTags(
                    "application", "${spring.application.name:my-app}",
                    "instance", "${app.instance.id:${random.value}}",
                    "version", "${app.version:1.0.0}",
                    "environment", "${app.environment:dev}"
                );

            // Настройка экспорта метрик
            registry.config()
                .meterFilter(new MeterFilter() {
                    @Override
                    public MeterFilterReply accept(Meter.Id id) {
                        // Исключаем чувствительные метрики
                        if (id.getName().contains("password") ||
                            id.getName().contains("secret")) {
                            return MeterFilterReply.DENY;
                        }

                        // Ограничиваем кардинальность
                        if (id.getTags().size() > 10) {
                            log.warn("High cardinality detected for metric: {}", id);
                            return MeterFilterReply.DENY;
                        }

                        return MeterFilterReply.NEUTRAL;
                    }
                });
        };
    }

    @Bean
    public MeterBinder healthMetricsBinder(HealthEndpoint healthEndpoint) {
        return registry -> {
            // Метрики здоровья приложения
            Gauge.builder("application.health.status", () -> {
                Health health = healthEndpoint.health();
                return health.getStatus().equals(Status.UP) ? 1.0 : 0.0;
            })
            .description("Application health status (1=UP, 0=DOWN)")
            .register(registry);

            // Метрики для компонентов
            healthEndpoint.health().getComponents().forEach((name, component) -> {
                Gauge.builder("application.health.component", () -> {
                    return component.getStatus().equals(Status.UP) ? 1.0 : 0.0;
                })
                .description("Health status of component: " + name)
                .tags("component", name)
                .register(registry);
            });
        };
    }

    @Bean
    public MeterBinder businessMetricsBinder(OrderService orderService) {
        return registry -> {
            // SLA метрики
            registry.gauge("business.sla.orders.per.minute",
                orderService, OrderService::getOrdersPerMinute);

            registry.gauge("business.sla.average.processing.time",
                orderService, OrderService::getAverageProcessingTime);

            // Бизнес KPIs
            registry.gauge("business.kpi.customer.satisfaction",
                orderService, OrderService::getCustomerSatisfactionScore);

            registry.gauge("business.kpi.revenue.per.hour",
                orderService, OrderService::getRevenuePerHour);
        };
    }
}

@Service
@Slf4j
public class AlertingService {

    private final MeterRegistry meterRegistry;

    public AlertingService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    // Проверка порогов и алертинг
    @Scheduled(fixedRate = 30000) // Каждые 30 секунд
    public void checkThresholds() {
        checkErrorRate();
        checkResponseTime();
        checkResourceUsage();
    }

    private void checkErrorRate() {
        Counter errorCounter = meterRegistry.find("http.requests")
            .tag("status", "500")
            .counter();

        if (errorCounter != null) {
            double errorRate = errorCounter.count();

            if (errorRate > 10) { // Больше 10 ошибок за период
                log.error("ALERT: High error rate detected: {} 5xx errors", errorRate);
                // Отправить алерт в PagerDuty, Slack и т.д.
                sendAlert("High Error Rate", "Error rate exceeded threshold: " + errorRate);
            }
        }
    }

    private void checkResponseTime() {
        Timer timer = meterRegistry.find("http.requests.duration").timer();

        if (timer != null) {
            double p95 = timer.percentile(0.95, TimeUnit.MILLISECONDS);

            if (p95 > 2000) { // 95-й перцентиль > 2 секунды
                log.warn("ALERT: Slow response time detected: {}ms (95th percentile)", p95);
                sendAlert("Slow Response Time", "95th percentile response time: " + p95 + "ms");
            }
        }
    }

    private void checkResourceUsage() {
        Gauge memoryGauge = meterRegistry.find("jvm.memory.used")
            .tag("area", "heap")
            .gauge();

        if (memoryGauge != null) {
            double usedMemory = memoryGauge.value();
            double maxMemory = getMaxHeapMemory();

            double usagePercent = (usedMemory / maxMemory) * 100;

            if (usagePercent > 90) { // Использование памяти > 90%
                log.error("ALERT: High memory usage: {}%", usagePercent);
                sendAlert("High Memory Usage", "Memory usage: " + usagePercent + "%");
            }
        }
    }

    private double getMaxHeapMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    private void sendAlert(String title, String message) {
        // Интеграция с системами алертинга
        // - PagerDuty
        // - OpsGenie
        // - Slack
        // - Email
        // - SMS

        log.info("Alert sent - {}: {}", title, message);
    }
}
```


## Заключение

**Micrometer** — это современная и библиотека для создания и экспорта метрик в **Java** приложениях. Она обеспечивает **vendor-neutral** подход к мониторингу и поддерживает большинство популярных систем мониторинга.

### Преимущества Micrometer

1. **Vendor neutral** — Единый **API** для всех систем мониторинга
2. **Dimensional metrics** — Поддержка тегов и измерений
3. **Spring `Boot` интеграция** — Автоматическая конфигурация
4. **Reactive support** — Поддержка **reactive** приложений
5. **Performance optimized** — Низкие накладные расходы
6. **Extensible** — Легко добавить новые **registry**
7. **Backwards compatible** — Поддержка **legacy** систем
8. **Rich ecosystem** — Большое количество **integrations**

### Основные паттерны использования

1. **Counter паттерн** — Для подсчета событий
2. **Gauge паттерн** — Для измерения текущих значений
3. **Timer паттерн** — Для измерения времени выполнения
4. **Distribution `Summary` паттерн** — Для статистики распределения
5. **Tagging паттерн** — Для категоризации метрик
6. **Registry `Composition` паттерн** — Для множественных систем мониторинга

### Когда использовать Micrometer

**Рекомендуется:**
- **Enterprise** приложения с высокими требованиями к мониторингу
- Микросервисная архитектура
- Приложения с **complex** бизнес-метриками
- Системы с **distributed monitoring**
- Проекты с `CI/CD` и **DevOps** практиками

**Особенно полезно:**
- В **Spring Boot** приложениях (автоматическая интеграция)
- При работе с **Prometheus**, **InfluxDB**, **CloudWatch**
- Для создания **custom business metrics**
- В системах с **high availability** требованиями
- При реализации **alerting** и **monitoring**

### Сравнение с альтернативами

| Библиотека | Преимущества | Недостатки |
|------------|-------------|------------|
| **Micrometer** | **Vendor neutral**, **dimensional**, **Spring Boot support** | Более сложная настройка |
| **Dropwizard Metrics** | Простая, **functional** | Только **Java**, меньше **integrations** |
| **Metrics v3** | Продвинутые возможности | Сложность, меньше экосистемы |
| **Spectator (Netflix)** | **Netflix battle-tested** | Только **Netflix stack** |

**Micrometer** рекомендуется как основной выбор для метрик в современных **Java** приложениях, особенно в экосистеме **Spring Boot** и микросервисной архитектуре.


[⬆ Наверх](../)

## См. также

- [Apache HttpClient: Мощный HTTP клиент для Java](java-apache-httpclient.md)
- [Apache POI](java-apache-poi.md)
- [Bean Validation (JSR-380 / Jakarta Validation 3.0)](java-bean-validation.md)
- [HikariCP: Высокопроизводительный Connection Pool](java-hikaricp.md)
- [HTTP-клиенты в Java](java-http-clients.md)
