---
title: "Resilience4j: Fault Tolerance для Java"
description: "Комплексное руководство по использованию Resilience4j — легковесной библиотеки fault tolerance для Java приложений с паттернами Circuit Breaker, Rate Limiter, Retry и другими."
tags:
  - libraries
  - java
  - java-resilience4j
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Resilience4j: **Fault Tolerance** для **Java**

**Комплексное руководство по использованию `Resilience4j` — легковесной библиотеки fault tolerance для `Java` приложений с паттернами `Circuit Breaker`, `Rate Limiter`, `Retry` и другими.**

## Полезные ссылки

### Официальная документация
- [Resilience4j](https://resilience4j.readme.io/) — официальный сайт
- [Resilience4j GitHub](https://github.com/resilience4j/resilience4j) — репозиторий проекта
- [Resilience4j Documentation](https://resilience4j.readme.io/docs) — полная документация

### Интеграция
- [Spring Boot Resilience4j](https://docs.spring.io/spring-cloud-circuitbreaker/reference/spring-cloud-circuitbreaker-resilience4j.html) — **Spring Boot** интеграция
- [Micrometer Integration](https://resilience4j.readme.io/docs/micrometer) — метрики
- [Examples](https://github.com/resilience4j/resilience4j/tree/master/resilience4j-documentation) — примеры

## Содержание

- [Введение в Resilience4j](#введение-в-resilience4j)
  - [Почему Resilience4j?](#почему-resilience4j)
  - [Основные модули](#основные-модули)
  - [Resilience паттерны](#resilience-паттерны)
- [Установка и настройка](#установка-и-настройка)
  - [Maven](#maven)
  - [Gradle](#gradle)
  - [Базовая конфигурация](#базовая-конфигурация)
- [Circuit Breaker](#circuit-breaker)
  - [Основы Circuit Breaker](#основы-circuit-breaker)
  - [Конфигурация Circuit Breaker](#конфигурация-circuit-breaker)
  - [Использование Circuit Breaker с **fallback**](#использование-circuit-breaker-с-fallback)
- [Retry](#retry)
  - [Основы Retry](#основы-retry)
  - [Конфигурация Retry](#конфигурация-retry)
  - [Использование Retry с **Circuit Breaker**](#использование-retry-с-circuit-breaker)
- [Rate Limiter](#rate-limiter)
  - [Основы Rate Limiter](#основы-rate-limiter)
  - [Конфигурация Rate Limiter](#конфигурация-rate-limiter)
  - [Использование Rate Limiter для **API**](#использование-rate-limiter-для-api)
- [Bulkhead](#bulkhead)
  - [Основы Bulkhead](#основы-bulkhead)
  - [Конфигурация Bulkhead](#конфигурация-bulkhead)
  - [Использование Bulkhead для изоляции](#использование-bulkhead-для-изоляции)
- [Timeout](#timeout)
  - [Основы Timeout](#основы-timeout)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
  - [Автоматическая конфигурация](#автоматическая-конфигурация)
- [application.yml](#applicationyml)
  - [Анотационная конфигурация](#анотационная-конфигурация)
  - [Кастомные аспекты](#кастомные-аспекты)
- [Мониторинг и метрики](#мониторинг-и-метрики)
  - [Micrometer интеграция](#micrometer-интеграция)
  - [Health indicators](#health-indicators)
  - [Кастомные метрики](#кастомные-метрики)
- [Best practices](#best-practices)
  - [1. Правильная конфигурация Circuit Breaker](#1-правильная-конфигурация-circuit-breaker)
  - [2. Комбинирование паттернов](#2-комбинирование-паттернов)
  - [3. Асинхронные операции](#3-асинхронные-операции)
  - [4. Тестирование resilience механизмов](#4-тестирование-resilience-механизмов)
  - [5. Мониторинг в production](#5-мониторинг-в-production)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Заключение](#заключение)
  - [Преимущества Resilience4j](#преимущества-resilience4j)
  - [Основные паттерны использования](#основные-паттерны-использования)
  - [Когда использовать Resilience4j](#когда-использовать-resilience4j)
  - [Сравнение с альтернативами](#сравнение-с-альтернативами)

## Введение в **Resilience4j**

**Resilience4j** — это легковесная библиотека **fault tolerance** для **Java**, которая предоставляет высокопроизводительные и простые в использовании реализации популярных **resilience** паттернов: **Circuit Breaker**, **Retry**, **Rate Limiter**, **Bulkhead** и других.

### Почему **Resilience4j**?

**Resilience4j** предлагает множество преимуществ:**

1. **Легковесность** — Минимальные зависимости, высокая производительность
2. **Функциональное программирование** — Использование **functional interfaces**
3. **Reactive streams** — Поддержка **reactive programming**
4. **Модульная архитектура** — Использование только нужных модулей
5. **Spring `Boot` интеграция** — Простая конфигурация через **properties**
6. **Micrometer метрики** — Интеграция с системами мониторинга
7. **Thread safety** — Все компоненты **thread-safe**
8. **Активное развитие** — Регулярные обновления и улучшения

### Основные модули

- **resilience4j-circuitbreaker** — **Circuit Breaker** паттерн
- **resilience4j-retry** — **Retry** паттерн
- **resilience4j-ratelimiter** — **Rate Limiter** паттерн
- **resilience4j-bulkhead** — **Bulkhead** паттерн
- **resilience4j-time-limiter** — **Timeout** паттерн
- **resilience4j-cache** — **Cache** паттерн

### **Resilience** паттерны

**Circuit Breaker** — Предотвращает каскадные сбои, быстро отказывая при проблемах
**Retry** — Автоматически повторяет неудачные операции
**Rate Limiter** — Ограничивает частоту запросов
**Bulkhead** — Изолирует компоненты для предотвращения распространения сбоев
**Timeout** — Устанавливает максимальное время ожидания операций
**Cache** — Кэширует результаты для улучшения производительности

## Установка и настройка

### **Maven**

Зависимости **Maven** для **Resilience4j** (**Spring `Boot 2` и отдельные модули: circuitbreaker, retry, ratelimiter**).

```xml
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
    <version>2.0.2</version>
</dependency>

<!-- Отдельные модули -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-circuitbreaker</artifactId>
    <version>2.0.2</version>
</dependency>

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-retry</artifactId>
    <version>2.0.2</version>
</dependency>

<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-ratelimiter</artifactId>
    <version>2.0.2</version>
</dependency>
```

### **Gradle**

```kotlin
dependencies {
    implementation("io.github.resilience4j:resilience4j-spring-boot2:2.0.2")

    // Или отдельные модули
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:2.0.2")
    implementation("io.github.resilience4j:resilience4j-retry:2.0.2")
    implementation("io.github.resilience4j:resilience4j-ratelimiter:2.0.2")
}
```

### Базовая конфигурация

```java
/
 * Конфигурация Resilience4j компонентов для Spring Boot приложения
 * Настраивает все основные паттерны устойчивости: Circuit Breaker, Retry, Rate Limiter, Bulkhead, Timeout
 */
@Configuration
public class Resilience4jConfiguration {

    /
     * Circuit Breaker - предотвращает каскадные сбои
     * Быстро отказывает при обнаружении проблем с зависимостью
     * @return CircuitBreaker с настройками по умолчанию
     */
    @Bean
    public CircuitBreaker circuitBreaker() {
        // Создаем Circuit Breaker с именем "backendService" и настройками по умолчанию
        // По умолчанию: failureRateThreshold=50%, slidingWindowSize=100, waitDurationInOpenState=60s
        return CircuitBreaker.ofDefaults("backendService");
    }

    /
     * Retry - автоматически повторяет неудачные операции
     * Используется для временных сбоев сети или сервисов
     * @return Retry с настройками по умолчанию
     */
    @Bean
    public Retry retry() {
        // Создаем Retry с именем "backendService" и настройками по умолчанию
        // По умолчанию: maxAttempts=3, waitDuration=500ms
        return Retry.ofDefaults("backendService");
    }

    /
     * Rate Limiter - ограничивает частоту вызовов
     * Предотвращает перегрузку внешних сервисов
     * @return RateLimiter с настройками по умолчанию
     */
    @Bean
    public RateLimiter rateLimiter() {
        // Создаем Rate Limiter с именем "backendService" и настройками по умолчанию
        // По умолчанию: limitForPeriod=50, limitRefreshPeriod=500ms
        return RateLimiter.ofDefaults("backendService");
    }

    /
     * Bulkhead - изолирует ресурсы для предотвращения перегрузки
     * Ограничивает количество одновременных вызовов
     * @return Bulkhead с настройками по умолчанию
     */
    @Bean
    public Bulkhead bulkhead() {
        // Создаем Bulkhead с именем "backendService" и настройками по умолчанию
        // По умолчанию: maxConcurrentCalls=25, maxWaitDuration=0ms
        return Bulkhead.ofDefaults("backendService");
    }

    /
     * Time Limiter - устанавливает таймаут для операций
     * Прерывает долго выполняющиеся операции
     * @return TimeLimiter с таймаутом 5 секунд
     */
    @Bean
    public TimeLimiter timeLimiter() {
        // Создаем Time Limiter с таймаутом 5 секунд
        // Операции, выполняющиеся дольше 5 секунд, будут прерваны
        return TimeLimiter.of(Duration.ofSeconds(5));
    }
}
```

## **Circuit Breaker**

### Основы **Circuit Breaker**

**Circuit Breaker** предотвращает каскадные сбои, быстро отказывая при обнаружении проблем с зависимостью. Он имеет три состояния:**

- **CLOSED** — Нормальная работа, запросы проходят
- **OPEN** — Обнаружены проблемы, запросы быстро отказывают
- **HALF_OPEN** — Тестирование восстановления, пропускает ограниченное количество запросов

```java
/
 * Сервис для демонстрации использования Circuit Breaker
 * Показывает как защитить вызовы внешних сервисов от каскадных сбоев
 */
@Service
public class BackendService {

    // Circuit Breaker для защиты от сбоев внешнего сервиса
    private final CircuitBreaker circuitBreaker;

    /
     * Конструктор - инициализирует Circuit Breaker с настройками по умолчанию
     */
    public BackendService() {
        // Создаем Circuit Breaker с именем "backendService"
        // Это имя используется для идентификации в метриках и логах
        this.circuitBreaker = CircuitBreaker.ofDefaults("backendService");
    }

    /
     * Синхронный вызов внешнего сервиса с защитой Circuit Breaker
     * @return результат вызова внешнего сервиса
     * @throws RuntimeException если Circuit Breaker открыт или произошла ошибка
     */
    public String callBackend() {
        // Используем executeSupplier для синхронного выполнения
        // Circuit Breaker автоматически отслеживает успешные/неудачные вызовы
        return circuitBreaker.executeSupplier(() -> {
            // Имитация вызова внешнего сервиса
            // В реальном приложении здесь будет HTTP запрос или вызов другого сервиса
            return callExternalService();
        });
    }

    /
     * Асинхронный вызов внешнего сервиса с защитой Circuit Breaker
     * @return CompletionStage с результатом вызова
     */
    public CompletionStage<String> callBackendAsync() {
        // Используем executeCompletionStage для асинхронного выполнения
        // Подходит для неблокирующих операций
        return circuitBreaker.executeCompletionStage(() -> {
            // Асинхронный вызов через CompletableFuture
            // Выполняется в отдельном потоке из ForkJoinPool
            return CompletableFuture.supplyAsync(this::callExternalService);
        });
    }

    /
     * Имитация вызова внешнего сервиса
     * В реальном приложении здесь будет HTTP запрос, вызов БД или другого сервиса
     * @return строка с результатом
     * @throws RuntimeException с вероятностью 20% для имитации сбоев
     */
    private String callExternalService() {
        // Имитация внешнего вызова с вероятностью сбоя 20%
        // Это используется для демонстрации работы Circuit Breaker
        if (Math.random() > 0.8) {
            // Имитируем сбой сервиса
            throw new RuntimeException("Service unavailable");
        }
        // Успешный ответ
        return "Success";
    }
}
```

### Конфигурация **Circuit Breaker**

```java
/
 * Расширенная конфигурация Circuit Breaker с кастомными настройками
 * Демонстрирует различные параметры настройки для разных сценариев использования
 */
@Configuration
public class CircuitBreakerConfiguration {

    /
     * Circuit Breaker с кастомными настройками для критичных сервисов
     * Более строгие пороги для быстрого обнаружения проблем
     * @return настроенный Circuit Breaker
     */
    @Bean
    public CircuitBreaker customCircuitBreaker() {
        // Создаем кастомную конфигурацию Circuit Breaker
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            // Настройки failure threshold - пороги для открытия circuit
            .failureRateThreshold(50) // Circuit открывается если 50% вызовов завершились ошибкой
            .slowCallRateThreshold(50) // Circuit открывается если 50% вызовов медленные
            .slowCallDurationThreshold(Duration.ofSeconds(2)) // Медленный вызов - это вызов > 2 секунд

            // Настройки sliding window - окно для анализа вызовов
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED) // Тип окна: COUNT_BASED (по количеству) или TIME_BASED (по времени)
            .slidingWindowSize(10) // Анализируем последние 10 вызовов для принятия решения

            // Настройки recovery - восстановление после сбоя
            .waitDurationInOpenState(Duration.ofSeconds(10)) // Ждать 10 секунд в OPEN состоянии перед переходом в HALF_OPEN
            .permittedNumberOfCallsInHalfOpenState(3) // Разрешить 3 тестовых вызова в HALF_OPEN состоянии
            .minimumNumberOfCalls(5) // Минимум 5 вызовов нужно для расчета статистики

            // Настройки автоматического перехода
            .automaticTransitionFromOpenToHalfOpenEnabled(true) // Автоматически переходить из OPEN в HALF_OPEN после waitDuration

            .build(); // Строим конфигурацию

        // Создаем Circuit Breaker с кастомной конфигурацией
        return CircuitBreaker.of("customCircuitBreaker", config);
    }

    /
     * Circuit Breaker с fallback и логированием событий
     * Регистрирует слушателей для мониторинга состояния Circuit Breaker
     * @return Circuit Breaker с event listeners
     */
    @Bean
    public CircuitBreaker circuitBreakerWithFallback() {
        // Создаем Circuit Breaker с настройками по умолчанию
        CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("fallbackService");

        // Регистрация слушателей событий для мониторинга и логирования
        circuitBreaker.getEventPublisher()
            // Логируем успешные вызовы
            .onSuccess(event -> log.info("Circuit breaker success: {}", event))
            // Логируем ошибки
            .onError(event -> log.warn("Circuit breaker error: {}", event))
            // Логируем переходы между состояниями (CLOSED -> OPEN -> HALF_OPEN -> CLOSED)
            .onStateTransition(event -> log.info("State transition: {} -> {}",
                event.getStateTransition().getFromState(), // Исходное состояние
                event.getStateTransition().getToState())); // Новое состояние

        return circuitBreaker;
    }
}
```

### Использование **Circuit Breaker** с **fallback**

```java
/
 * Сервис с устойчивостью к сбоям, демонстрирующий использование Circuit Breaker с fallback
 * Показывает два подхода: ручной fallback и декорированный fallback
 */
@Service
@Slf4j
public class ResilientService {

    // Circuit Breaker для защиты от сбоев внешнего сервиса
    private final CircuitBreaker circuitBreaker;

    /
     * Конструктор с внедрением Circuit Breaker через DI
     * @param circuitBreaker Circuit Breaker компонент
     */
    public ResilientService(CircuitBreaker circuitBreaker) {
        this.circuitBreaker = circuitBreaker;
    }

    /
     * Вызов внешнего сервиса с ручной обработкой fallback
     * Демонстрирует явную обработку исключений Circuit Breaker
     * @return результат вызова или fallback данные
     */
    public String callWithFallback() {
        // Декорируем Supplier с Circuit Breaker логикой
        // Circuit Breaker будет отслеживать успешные/неудачные вызовы
        Supplier<String> decoratedSupplier = circuitBreaker.decorateSupplier(() -> {
            // Вызываем внешний сервис
            // Circuit Breaker может заблокировать вызов если он в OPEN состоянии
            return callExternalService();
        });

        // Создаем fallback Supplier для использования при сбоях
        Supplier<String> fallbackSupplier = () -> {
            log.warn("Circuit breaker is OPEN, using fallback");
            // Возвращаем запасные данные вместо реального ответа сервиса
            return getFallbackData();
        };

        try {
            // Пытаемся выполнить декорированный вызов
            // Если Circuit Breaker в CLOSED или HALF_OPEN состоянии, вызов пройдет
            return decoratedSupplier.get();
        } catch (CallNotPermittedException e) {
            // Circuit Breaker заблокировал вызов (состояние OPEN)
            // Это означает что сервис недоступен и нужно использовать fallback
            log.warn("Circuit breaker prevented call: {}", e.getMessage());
            return fallbackSupplier.get();
        } catch (Exception e) {
            // Произошла другая ошибка при вызове сервиса
            // Также используем fallback для обеспечения отказоустойчивости
            log.error("Service call failed: {}", e.getMessage());
            return fallbackSupplier.get();
        }
    }

    /
     * Вызов внешнего сервиса с декорированным fallback через Decorators API
     * Более декларативный подход с автоматической обработкой исключений
     * @return результат вызова или fallback данные
     */
    public String callWithDecoratedFallback() {
        // Используем Decorators API для комбинирования нескольких паттернов устойчивости
        Supplier<String> decoratedSupplier = Decorators.ofSupplier(() -> callExternalService())
            // Добавляем Circuit Breaker защиту
            .withCircuitBreaker(circuitBreaker)
            // Добавляем fallback функцию, которая вызывается при любом исключении
            .withFallback((exception) -> {
                // Логируем причину срабатывания fallback
                log.warn("Fallback triggered due to: {}", exception.getMessage());
                // Возвращаем запасные данные
                return getFallbackData();
            })
            .decorate(); // Строим декорированный Supplier

        // Выполняем декорированный Supplier
        // Fallback автоматически сработает при любом исключении
        return decoratedSupplier.get();
    }

    /
     * Имитация вызова внешнего сервиса
     * В реальном приложении здесь будет HTTP запрос, вызов БД или другого микросервиса
     * @return строка с результатом вызова
     * @throws RuntimeException с вероятностью 30% для демонстрации сбоев
     */
    private String callExternalService() {
        // Имитация внешнего вызова с вероятностью сбоя 30%
        // Используется для демонстрации работы Circuit Breaker и fallback
        if (Math.random() > 0.7) {
            // Имитируем сбой внешнего сервиса
            throw new RuntimeException("External service error");
        }
        // Успешный ответ от внешнего сервиса
        return "External service response";
    }

    /
     * Получение запасных данных для fallback сценария
     * В реальном приложении здесь может быть:
     * - Кэшированные данные
     * - Дефолтные значения
     * - Данные из другого источника
     * @return строка с fallback данными
     */
    private String getFallbackData() {
        // Возврат кэшированных данных или дефолтных значений
        // Это позволяет приложению продолжать работать даже при сбое внешнего сервиса
        return "Fallback response";
    }
}
```

## **Retry**

### Основы **Retry**

**Retry** автоматически повторяет неудачные операции с настраиваемой стратегией повторов.

```java
@Service
public class RetryService {

    private final Retry retry;

    public RetryService() {
        this.retry = Retry.ofDefaults("externalService");
    }

    public String callWithRetry() {
        Supplier<String> decoratedSupplier = retry.decorateSupplier(() -> {
            return callUnreliableService();
        });

        return decoratedSupplier.get();
    }

    /
     * Асинхронный вызов ненадежного сервиса с автоматическими повторами
     * Используется для неблокирующих операций
     * @return CompletionStage с результатом после успешной попытки
     */
    public CompletionStage<String> callWithRetryAsync() {
        // Декорируем Supplier, возвращающий CompletionStage
        // Retry будет повторять асинхронные операции при сбоях
        Supplier<CompletionStage<String>> decoratedSupplier =
            retry.decorateSupplier(() -> callUnreliableServiceAsync());

        // Выполняем декорированный Supplier
        // Retry автоматически обработает повторы для асинхронных операций
        return decoratedSupplier.get();
    }

    /
     * Имитация ненадежного сервиса с временными сбоями
     * В реальном приложении здесь будет вызов внешнего API или БД
     * @return строка с результатом
     * @throws RuntimeException с вероятностью 20% для имитации временных сбоев
     */
    private String callUnreliableService() {
        // Имитация ненадежного сервиса с вероятностью временного сбоя 20%
        // Такие сбои обычно можно исправить повторной попыткой
        if (Math.random() > 0.8) {
            // Имитируем временный сбой (сетевой таймаут, временная недоступность и т.д.)
            throw new RuntimeException("Temporary failure");
        }
        // Успешный ответ от сервиса
        return "Success";
    }

    /
     * Асинхронная версия вызова ненадежного сервиса
     * Выполняется в отдельном потоке из ForkJoinPool
     * @return CompletionStage с результатом вызова
     */
    private CompletionStage<String> callUnreliableServiceAsync() {
        // Создаем асинхронный вызов через CompletableFuture
        // Выполняется в отдельном потоке из ForkJoinPool.commonPool()
        return CompletableFuture.supplyAsync(this::callUnreliableService);
    }
}
```

### Конфигурация **Retry**

```java
@Configuration
public class RetryConfiguration {

    @Bean
    public Retry exponentialBackoffRetry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(5) // Максимум 5 попыток
            .waitDuration(Duration.ofMillis(100)) // Начальная задержка 100ms
            .intervalBiFunction((attempt, interval) -> interval * 2) // Экспоненциальная задержка
            .retryOnException(throwable -> {
                // Повторять только для определенных исключений
                return throwable instanceof RuntimeException &&
                       throwable.getMessage().contains("Temporary");
            })
            .ignoreExceptions(IllegalArgumentException.class) // Не повторять для этих исключений
            .failAfterMaxAttempts(true) // Выбрасывать исключение после maxAttempts
            .build();

        return Retry.of("exponentialBackoffRetry", config);
    }

    @Bean
    public Retry fixedDelayRetry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofSeconds(1)) // Фиксированная задержка 1 сек
            .retryOnResult(response -> {
                // Повторять на основе результата
                return response == null || response.equals("ERROR");
            })
            .build();

        return Retry.of("fixedDelayRetry", config);
    }
}
```

### Использование **Retry** с **Circuit Breaker**

```java
@Service
@Slf4j
public class CombinedResilienceService {

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public CombinedResilienceService() {
        this.circuitBreaker = CircuitBreaker.ofDefaults("combinedService");
        this.retry = Retry.ofDefaults("combinedService");
    }

    public String callWithRetryAndCircuitBreaker() {
        Supplier<String> decoratedSupplier = Decorators.ofSupplier(() -> callExternalService())
            .withRetry(retry)
            .withCircuitBreaker(circuitBreaker)
            .withFallback(throwable -> {
                log.warn("All resilience mechanisms failed: {}", throwable.getMessage());
                return getEmergencyFallback();
            })
            .decorate();

        return decoratedSupplier.get();
    }

    // Настройка событий
    @PostConstruct
    public void setupEventListeners() {
        retry.getEventPublisher()
            .onRetry(event -> log.info("Retry attempt {}: {}",
                event.getNumberOfRetryAttempts(), event.getLastThrowable().getMessage()));

        circuitBreaker.getEventPublisher()
            .onStateTransition(event -> log.warn("Circuit breaker state changed: {} -> {}",
                event.getStateTransition().getFromState(),
                event.getStateTransition().getToState()));
    }

    private String callExternalService() {
        // Имитация внешнего вызова
        double random = Math.random();
        if (random > 0.9) {
            throw new RuntimeException("Service permanently down");
        } else if (random > 0.7) {
            throw new RuntimeException("Temporary network issue");
        }
        return "Success";
    }

    private String getEmergencyFallback() {
        return "Emergency fallback data";
    }
}
```

## **Rate Limiter**

### Основы **Rate Limiter**

**Rate Limiter** ограничивает частоту выполнения операций, предотвращая перегрузку зависимостей.

```java
@Service
public class RateLimitedService {

    private final RateLimiter rateLimiter;

    public RateLimitedService() {
        this.rateLimiter = RateLimiter.ofDefaults("apiService");
    }

    public String callRateLimitedService() {
        return rateLimiter.executeSupplier(() -> {
            return callExternalAPI();
        });
    }

    public CompletionStage<String> callRateLimitedAsync() {
        return rateLimiter.executeCompletionStage(() -> {
            return CompletableFuture.supplyAsync(this::callExternalAPI);
        });
    }

    private String callExternalAPI() {
        // Имитация вызова API
        return "API Response";
    }
}
```

### Конфигурация **Rate Limiter**

```java
@Configuration
public class RateLimiterConfiguration {

    @Bean
    public RateLimiter tokenBucketRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitForPeriod(10) // 10 разрешений
            .limitRefreshPeriod(Duration.ofSeconds(1)) // За 1 секунду
            .timeoutDuration(Duration.ofMillis(100)) // Таймаут ожидания разрешения
            .build();

        return RateLimiter.of("tokenBucketLimiter", config);
    }

    @Bean
    public RateLimiter smoothRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .limitForPeriod(5) // 5 разрешений в секунду
            .timeoutDuration(Duration.ofMillis(500))
            .whenFull(RateLimiter.EventPublisher.EventPublisherIdle) // Поведение при исчерпании
            .build();

        return RateLimiter.of("smoothLimiter", config);
    }

    @Bean
    public RateLimiter burstyRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
            .limitRefreshPeriod(Duration.ofSeconds(10))
            .limitForPeriod(100) // 100 разрешений за 10 секунд
            .timeoutDuration(Duration.ofMillis(0)) // Без ожидания
            .build();

        return RateLimiter.of("burstyLimiter", config);
    }
}
```

### Использование **Rate Limiter** для **API**

```java
@RestController
@Slf4j
public class ApiController {

    private final RateLimiter apiRateLimiter;
    private final RateLimiter adminRateLimiter;

    public ApiController() {
        this.apiRateLimiter = RateLimiter.of("apiLimiter",
            RateLimiterConfig.custom()
                .limitForPeriod(100) // 100 запросов
                .limitRefreshPeriod(Duration.ofMinutes(1)) // В минуту
                .timeoutDuration(Duration.ofSeconds(1))
                .build());

        this.adminRateLimiter = RateLimiter.of("adminLimiter",
            RateLimiterConfig.custom()
                .limitForPeriod(1000) // 1000 запросов для админов
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ofSeconds(1))
                .build());
    }

    @GetMapping("/api/data")
    public ResponseEntity<String> getData(@RequestHeader(value = "X-API-Key", required = false) String apiKey) {

        RateLimiter limiter = isAdminKey(apiKey) ? adminRateLimiter : apiRateLimiter;

        try {
            return limiter.executeSupplier(() -> {
                log.info("Processing API request");
                return ResponseEntity.ok("Data retrieved successfully");
            });
        } catch (RequestNotPermitted e) {
            log.warn("Rate limit exceeded for API key: {}", apiKey);
            return ResponseEntity.status(429)
                .header("X-Rate-Limit-Retry-After", "60")
                .body("Too many requests. Please try again later.");
        }
    }

    @GetMapping("/api/status")
    public ResponseEntity<Map<String, Object>> getRateLimitStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("apiLimiterAvailablePermissions", apiRateLimiter.getAvailablePermissions());
        status.put("adminLimiterAvailablePermissions", adminRateLimiter.getAvailablePermissions());

        return ResponseEntity.ok(status);
    }

    private boolean isAdminKey(String apiKey) {
        return apiKey != null && apiKey.startsWith("admin-");
    }
}
```

## **Bulkhead**

### Основы **Bulkhead**

**Bulkhead** изолирует компоненты, предотвращая распространение сбоев и перегрузок.

```java
@Service
public class BulkheadService {

    private final Bulkhead bulkhead;

    public BulkheadService() {
        this.bulkhead = Bulkhead.ofDefaults("databaseService");
    }

    public List<User> getUsersBulkheaded() {
        Supplier<List<User>> decoratedSupplier = bulkhead.decorateSupplier(() -> {
            return queryUsersFromDatabase();
        });

        return decoratedSupplier.get();
    }

    public CompletionStage<List<User>> getUsersBulkheadedAsync() {
        Supplier<CompletionStage<List<User>>> decoratedSupplier =
            bulkhead.decorateSupplier(() -> queryUsersAsync());

        return decoratedSupplier.get();
    }

    private List<User> queryUsersFromDatabase() {
        // Имитация тяжелого запроса к БД
        try {
            Thread.sleep(100); // Имитация задержки
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Arrays.asList(new User("John"), new User("Jane"));
    }

    private CompletionStage<List<User>> queryUsersAsync() {
        return CompletableFuture.supplyAsync(this::queryUsersFromDatabase);
    }
}
```

### Конфигурация **Bulkhead**

```java
@Configuration
public class BulkheadConfiguration {

    @Bean
    public Bulkhead semaphoreBulkhead() {
        BulkheadConfig config = BulkheadConfig.custom()
            .maxConcurrentCalls(10) // Максимум 10 одновременных вызовов
            .maxWaitDuration(Duration.ofMillis(500)) // Максимум ожидания 500ms
            .build();

        return Bulkhead.of("semaphoreBulkhead", config);
    }

    @Bean
    public ThreadPoolBulkhead threadPoolBulkhead() {
        ThreadPoolBulkheadConfig config = ThreadPoolBulkheadConfig.custom()
            .maxThreadPoolSize(5) // Максимум 5 потоков
            .coreThreadPoolSize(2) // Минимум 2 потока
            .queueCapacity(10) // Размер очереди 10
            .keepAliveDuration(Duration.ofMinutes(1)) // Время жизни потока
            .build();

        return ThreadPoolBulkhead.of("threadPoolBulkhead", config);
    }

    @Bean
    public Bulkhead monitoredBulkhead() {
        Bulkhead bulkhead = Bulkhead.ofDefaults("monitoredService");

        // Настройка мониторинга
        bulkhead.getEventPublisher()
            .onCallPermitted(event -> log.debug("Call permitted"))
            .onCallRejected(event -> log.warn("Call rejected - bulkhead full"))
            .onCallFinished(event -> log.debug("Call finished"));

        return bulkhead;
    }
}
```

### Использование **Bulkhead** для изоляции

```java
@Service
@Slf4j
public class IsolatedService {

    private final Bulkhead databaseBulkhead;
    private final Bulkhead externalApiBulkhead;
    private final ThreadPoolBulkhead heavyComputationBulkhead;

    public IsolatedService() {
        this.databaseBulkhead = Bulkhead.of("database",
            BulkheadConfig.custom().maxConcurrentCalls(5).build());

        this.externalApiBulkhead = Bulkhead.of("externalApi",
            BulkheadConfig.custom().maxConcurrentCalls(3).build());

        this.heavyComputationBulkhead = ThreadPoolBulkhead.of("computation",
            ThreadPoolBulkheadConfig.custom()
                .maxThreadPoolSize(2)
                .coreThreadPoolSize(1)
                .queueCapacity(5)
                .build());
    }

    public User getUserFromDatabase(Long userId) {
        Supplier<User> decoratedSupplier = Decorators.ofSupplier(() -> {
                return databaseService.findUserById(userId);
            })
            .withBulkhead(databaseBulkhead)
            .withFallback(throwable -> {
                log.warn("Database call failed, returning cached user: {}", throwable.getMessage());
                return getCachedUser(userId);
            })
            .decorate();

        return decoratedSupplier.get();
    }

    public WeatherData getWeatherFromExternalAPI(String city) {
        Supplier<WeatherData> decoratedSupplier = Decorators.ofSupplier(() -> {
                return weatherApiService.getWeather(city);
            })
            .withBulkhead(externalApiBulkhead)
            .withRetry(Retry.ofDefaults("weatherApi"))
            .withFallback(throwable -> {
                log.warn("Weather API call failed: {}", throwable.getMessage());
                return getDefaultWeather(city);
            })
            .decorate();

        return decoratedSupplier.get();
    }

    public ComputationResult performHeavyComputation(ComputationInput input) {
        Supplier<CompletionStage<ComputationResult>> decoratedSupplier =
            Decorators.ofSupplier(() -> performComputationAsync(input))
                .withThreadPoolBulkhead(heavyComputationBulkhead)
                .decorate();

        try {
            return decoratedSupplier.get().get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Heavy computation failed: {}", e.getMessage());
            throw new RuntimeException("Computation failed", e);
        }
    }

    private CompletionStage<ComputationResult> performComputationAsync(ComputationInput input) {
        return CompletableFuture.supplyAsync(() -> {
            // Имитация тяжелых вычислений
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return new ComputationResult("computed");
        });
    }
}
```

## **Timeout**

### Основы **Timeout**

**Timeout** устанавливает максимальное время ожидания для операций.

```java
@Service
public class TimeoutService {

    private final TimeLimiter timeLimiter;

    public TimeoutService() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        this.timeLimiter = TimeLimiter.of(Duration.ofSeconds(5));
    }

    public String callWithTimeout() {
        Supplier<String> decoratedSupplier = timeLimiter.decorateSupplier(() -> {
            return callSlowService();
        });

        try {
            return decoratedSupplier.get();
        } catch (Exception e) {
            log.warn("Call timed out: {}", e.getMessage());
            return getFallbackResult();
        }
    }

    public CompletionStage<String> callWithTimeoutAsync() {
        Supplier<CompletionStage<String>> decoratedSupplier =
            timeLimiter.decorateSupplier(this::callSlowServiceAsync);

        return decoratedSupplier.get();
    }

    private String callSlowService() {
        // Имитация медленного сервиса
        try {
            Thread.sleep(3000); // 3 секунды
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "Slow service response";
    }

    private CompletionStage<String> callSlowServiceAsync() {
        return CompletableFuture.supplyAsync(this::callSlowService);
    }

    private String getFallbackResult() {
        return "Timeout fallback";
    }
}
```

## Интеграция с **Spring Boot**

### Автоматическая конфигурация

```yaml
# application.yml
resilience4j:
  circuitbreaker:
    configs:
      default:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10000
        permittedNumberOfCallsInHalfOpenState: 3
    instances:
      backendA:
        baseConfig: default
      backendB:
        baseConfig: default
        waitDurationInOpenState: 5000

  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 500
    instances:
      backendA:
        baseConfig: default
      backendB:
        baseConfig: default
        maxAttempts: 5

  ratelimiter:
    configs:
      default:
        limitForPeriod: 10
        limitRefreshPeriod: 1000
        timeoutDuration: 1000
    instances:
      api:
        baseConfig: default
      adminApi:
        baseConfig: default
        limitForPeriod: 100
```

### Анотационная конфигурация

```java
@Configuration
@EnableAspectJAutoProxy
public class AspectConfiguration {

    @Bean
    public Resilience4jAspect resilience4jAspect() {
        return new Resilience4jAspect();
    }
}

@Service
@Slf4j
public class AnnotatedService {

    @CircuitBreaker(name = "backendA", fallbackMethod = "fallback")
    public String callBackendA() {
        return backendAService.call();
    }

    @CircuitBreaker(name = "backendB")
    @Retry(name = "backendB")
    @RateLimiter(name = "api")
    public String callBackendB() {
        return backendBService.call();
    }

    @Bulkhead(name = "database")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @RateLimiter(name = "api", fallbackMethod = "rateLimitFallback")
    public String protectedApiCall() {
        return apiService.call();
    }

    // Fallback методы
    public String fallback(Throwable t) {
        log.warn("Circuit breaker fallback: {}", t.getMessage());
        return "Fallback response";
    }

    public String rateLimitFallback(Throwable t) {
        log.warn("Rate limit exceeded: {}", t.getMessage());
        return "Too many requests";
    }
}
```

### Кастомные аспекты

```java
@Aspect
@Component
@Slf4j
public class CustomResilienceAspect {

    @Around("@annotation(circuitBreaker)")
    public Object circuitBreakerAround(ProceedingJoinPoint joinPoint, CircuitBreaker circuitBreaker) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        try {
            log.debug("Executing method {} with circuit breaker", methodName);
            return circuitBreaker.executeCheckedSupplier(() -> joinPoint.proceed());
        } catch (Throwable t) {
            log.warn("Circuit breaker {} triggered for method {}", circuitBreaker.name(), methodName);
            throw t;
        }
    }

    @Around("@annotation(retry)")
    public Object retryAround(ProceedingJoinPoint joinPoint, Retry retry) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        return retry.executeCheckedSupplier(() -> {
            log.debug("Retrying method {}", methodName);
            return joinPoint.proceed();
        });
    }

    @Around("@annotation(rateLimiter)")
    public Object rateLimiterAround(ProceedingJoinPoint joinPoint, RateLimiter rateLimiter) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        try {
            log.debug("Rate limiting method {}", methodName);
            return rateLimiter.executeCheckedSupplier(() -> joinPoint.proceed());
        } catch (RequestNotPermitted e) {
            log.warn("Rate limit exceeded for method {}", methodName);
            throw e;
        }
    }
}
```

## Мониторинг и метрики

### **Micrometer** интеграция

```java
@Configuration
public class MetricsConfiguration {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(MeterRegistry meterRegistry) {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();

        // Регистрация всех circuit breakers для мониторинга
        registry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            CircuitBreakerMetrics.ofCircuitBreakerRegistry(registry).bindTo(meterRegistry);
        });

        return registry;
    }

    @Bean
    public RetryRegistry retryRegistry(MeterRegistry meterRegistry) {
        RetryRegistry registry = RetryRegistry.ofDefaults();
        RetryMetrics.ofRetryRegistry(registry).bindTo(meterRegistry);
        return registry;
    }

    @Bean
    public RateLimiterRegistry rateLimiterRegistry(MeterRegistry meterRegistry) {
        RateLimiterRegistry registry = RateLimiterRegistry.ofDefaults();
        RateLimiterMetrics.ofRateLimiterRegistry(registry).bindTo(meterRegistry);
        return registry;
    }

    @Bean
    public BulkheadRegistry bulkheadRegistry(MeterRegistry meterRegistry) {
        BulkheadRegistry registry = BulkheadRegistry.ofDefaults();
        BulkheadMetrics.ofBulkheadRegistry(registry).bindTo(meterRegistry);
        return registry;
    }
}
```

### **Health indicators**

```java
@Component
public class ResilienceHealthIndicator implements HealthIndicator {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ResilienceHealthIndicator(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Override
    public Health health() {
        Collection<CircuitBreaker> circuitBreakers = circuitBreakerRegistry.getAllCircuitBreakers();

        for (CircuitBreaker circuitBreaker : circuitBreakers) {
            CircuitBreaker.State state = circuitBreaker.getState();

            if (state == CircuitBreaker.State.OPEN) {
                return Health.down()
                    .withDetail("circuitBreaker", circuitBreaker.getName())
                    .withDetail("state", state.name())
                    .withDetail("failureRate", circuitBreaker.getMetrics().getFailureRate())
                    .build();
            }

            if (state == CircuitBreaker.State.HALF_OPEN) {
                return Health.unknown()
                    .withDetail("circuitBreaker", circuitBreaker.getName())
                    .withDetail("state", state.name())
                    .build();
            }
        }

        return Health.up()
            .withDetail("circuitBreakers", circuitBreakers.size())
            .build();
    }
}
```

### Кастомные метрики

```java
@Service
@Slf4j
public class ResilienceMonitoringService {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final MeterRegistry meterRegistry;

    public ResilienceMonitoringService(CircuitBreakerRegistry circuitBreakerRegistry,
                                     MeterRegistry meterRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedRate = 30000) // Каждые 30 секунд
    public void logResilienceMetrics() {
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(this::logCircuitBreakerMetrics);
    }

    private void logCircuitBreakerMetrics(CircuitBreaker circuitBreaker) {
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

        log.info("Circuit Breaker [{}]: State={}, Calls={}, Failures={}, Successes={}",
            circuitBreaker.getName(),
            circuitBreaker.getState(),
            metrics.getNumberOfCalls(),
            metrics.getNumberOfFailedCalls(),
            metrics.getNumberOfSuccessfulCalls()
        );

        // Кастомные метрики
        meterRegistry.gauge("resilience4j.circuitbreaker.failure_rate",
            Tags.of("name", circuitBreaker.getName()),
            metrics, CircuitBreaker.Metrics::getFailureRate);
    }

    public Map<String, Object> getDetailedMetrics() {
        Map<String, Object> detailedMetrics = new HashMap<>();

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();

            Map<String, Object> circuitBreakerMetrics = new HashMap<>();
            circuitBreakerMetrics.put("state", circuitBreaker.getState().name());
            circuitBreakerMetrics.put("calls", metrics.getNumberOfCalls());
            circuitBreakerMetrics.put("failures", metrics.getNumberOfFailedCalls());
            circuitBreakerMetrics.put("successes", metrics.getNumberOfSuccessfulCalls());
            circuitBreakerMetrics.put("failureRate", metrics.getFailureRate());
            circuitBreakerMetrics.put("slowCalls", metrics.getNumberOfSlowCalls());

            detailedMetrics.put(circuitBreaker.getName(), circuitBreakerMetrics);
        });

        return detailedMetrics;
    }
}
```

## **Best practices**

### 1. Правильная конфигурация **Circuit Breaker**

```java
// ✅ Хорошо - продуманная конфигурация
@Configuration
public class CircuitBreakerBestPractices {

    @Bean
    public CircuitBreaker apiCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            // Настройки обнаружения проблем
            .failureRateThreshold(50.0f) // Открывать при 50% ошибок
            .slowCallRateThreshold(50.0f) // Открывать при 50% медленных вызовов
            .slowCallDurationThreshold(Duration.ofSeconds(3)) // Медленный вызов > 3 сек

            // Настройки окна наблюдения
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .slidingWindowSize(20) // Анализировать последние 20 вызовов

            // Настройки восстановления
            .waitDurationInOpenState(Duration.ofSeconds(30)) // Ждать 30 сек перед тестом
            .permittedNumberOfCallsInHalfOpenState(5) // 5 тестовых вызовов
            .minimumNumberOfCalls(10) // Минимум 10 вызовов для анализа

            // Настройки исключений
            .recordException(throwable -> {
                // Записывать только определенные исключения
                return throwable instanceof ConnectException ||
                       throwable instanceof TimeoutException ||
                       (throwable instanceof HttpStatusCodeException &&
                        ((HttpStatusCodeException) throwable).getStatusCode().is5xxServerError());
            })
            .build();

        return CircuitBreaker.of("apiCircuitBreaker", config);
    }

    // ❌ Плохо - дефолтные настройки для production
    @Bean
    public CircuitBreaker defaultCircuitBreaker() {
        return CircuitBreaker.ofDefaults("service"); // Не подходит для production
    }
}
```

### 2. Комбинирование паттернов

```java
// ✅ Хорошо - правильное комбинирование
@Service
@Slf4j
public class ResiliencePatternsCombination {

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final RateLimiter rateLimiter;
    private final Bulkhead bulkhead;

    public ResiliencePatternsCombination() {
        this.circuitBreaker = CircuitBreaker.ofDefaults("combined");
        this.retry = Retry.ofDefaults("combined");
        this.rateLimiter = RateLimiter.ofDefaults("combined");
        this.bulkhead = Bulkhead.ofDefaults("combined");
    }

    public String resilientCall() {
        Supplier<String> supplier = () -> callExternalService();

        Supplier<String> decoratedSupplier = Decorators.ofSupplier(supplier)
            // Сначала rate limiter (предотвращает перегрузку)
            .withRateLimiter(rateLimiter)
            // Затем bulkhead (изолирует вызовы)
            .withBulkhead(bulkhead)
            // Retry для временных ошибок
            .withRetry(retry)
            // Circuit breaker как последний рубеж
            .withCircuitBreaker(circuitBreaker)
            // Fallback для полной защиты
            .withFallback(throwable -> {
                log.error("All resilience mechanisms failed", throwable);
                return getEmergencyFallback();
            })
            .decorate();

        return decoratedSupplier.get();
    }

    // Правильный порядок применения паттернов
    public String callWithCorrectOrder() {
        return Decorators.ofSupplier(this::callExternalService)
            // 1. Rate Limiter - предотвратить перегрузку
            .withRateLimiter(rateLimiter)
            // 2. Bulkhead - изолировать ресурсы
            .withBulkhead(bulkhead)
            // 3. Retry - повторить временные ошибки
            .withRetry(retry)
            // 4. Circuit Breaker - быстро отказать при проблемах
            .withCircuitBreaker(circuitBreaker)
            .decorate()
            .get();
    }

    private String callExternalService() {
        // Имитация внешнего вызова
        return "External service response";
    }

    private String getEmergencyFallback() {
        return "Emergency fallback response";
    }
}
```

### 3. Асинхронные операции

```java
// ✅ Хорошо - асинхронные resilient операции
@Service
@Slf4j
public class AsyncResilienceService {

    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final Bulkhead bulkhead;

    public AsyncResilienceService() {
        this.circuitBreaker = CircuitBreaker.ofDefaults("async");
        this.retry = Retry.ofDefaults("async");
        this.bulkhead = Bulkhead.ofDefaults("async");
    }

    public CompletionStage<String> asyncResilientCall() {
        Supplier<CompletionStage<String>> supplier = () ->
            CompletableFuture.supplyAsync(this::callExternalService);

        Supplier<CompletionStage<String>> decoratedSupplier = Decorators.ofSupplier(supplier)
            .withBulkhead(bulkhead)
            .withRetry(retry)
            .withCircuitBreaker(circuitBreaker)
            .decorate();

        return decoratedSupplier.get();
    }

    public CompletionStage<String> callWithTimeout() {
        TimeLimiter timeLimiter = TimeLimiter.of(Duration.ofSeconds(5));

        Supplier<CompletionStage<String>> supplier = () ->
            CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(2000); // Имитация работы
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "Async result";
            });

        Supplier<CompletionStage<String>> decoratedSupplier =
            timeLimiter.decorateSupplier(supplier);

        return decoratedSupplier.get();
    }

    public CompletionStage<List<String>> batchCall(List<String> inputs) {
        List<Supplier<CompletionStage<String>>> suppliers = inputs.stream()
            .map(input -> (Supplier<CompletionStage<String>>) () ->
                CompletableFuture.supplyAsync(() -> processInput(input)))
            .collect(Collectors.toList());

        // Выполнить все вызовы с resilience
        return Futures.allOf(
            suppliers.stream()
                .map(supplier -> Decorators.ofSupplier(supplier)
                    .withCircuitBreaker(circuitBreaker)
                    .decorate()
                    .get())
                .collect(Collectors.toList())
        ).thenApply(results -> results.stream()
            .map(CompletionStage::toCompletableFuture)
            .map(CompletableFuture::join)
            .collect(Collectors.toList()));
    }

    private String callExternalService() {
        // Имитация внешнего вызова
        return "External response";
    }

    private String processInput(String input) {
        // Имитация обработки
        return "Processed: " + input;
    }
}
```

### 4. Тестирование **resilience** механизмов

```java
// ✅ Хорошо - тестирование resilience
@SpringBootTest
public class Resilience4jTest {

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Autowired
    private RetryRegistry retryRegistry;

    @Test
    public void testCircuitBreaker() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("test");

        // Тестирование успешных вызовов
        for (int i = 0; i < 5; i++) {
            circuitBreaker.onSuccess(1000); // Имитация успешного вызова
        }

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);

        // Тестирование ошибок
        for (int i = 0; i < 10; i++) {
            circuitBreaker.onError(1000, new RuntimeException("Test error"));
        }

        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    public void testRetry() {
        Retry retry = retryRegistry.retry("test");
        AtomicInteger attempts = new AtomicInteger(0);

        Supplier<String> supplier = () -> {
            int attempt = attempts.incrementAndGet();
            if (attempt < 3) {
                throw new RuntimeException("Attempt " + attempt + " failed");
            }
            return "Success";
        };

        Supplier<String> decoratedSupplier = retry.decorateSupplier(supplier);

        String result = decoratedSupplier.get();

        assertThat(result).isEqualTo("Success");
        assertThat(attempts.get()).isEqualTo(3); // 3 попытки: 2 неудачных + 1 успешная
    }

    @Test
    public void testRateLimiter() {
        RateLimiter rateLimiter = RateLimiter.of("test",
            RateLimiterConfig.custom()
                .limitForPeriod(2)
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .timeoutDuration(Duration.ofMillis(0))
                .build());

        // Первые 2 вызова должны пройти
        assertThat(rateLimiter.acquirePermission()).isTrue();
        assertThat(rateLimiter.acquirePermission()).isTrue();

        // Третий вызов должен быть заблокирован
        assertThat(rateLimiter.acquirePermission()).isFalse();
    }

    @Test
    public void testBulkhead() {
        Bulkhead bulkhead = Bulkhead.of("test",
            BulkheadConfig.custom()
                .maxConcurrentCalls(2)
                .maxWaitDuration(Duration.ofMillis(100))
                .build());

        // Должны пройти 2 одновременных вызова
        AtomicInteger concurrentCalls = new AtomicInteger(0);
        AtomicInteger maxConcurrent = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                bulkhead.executeSupplier(() -> {
                    int current = concurrentCalls.incrementAndGet();
                    maxConcurrent.updateAndGet(max -> Math.max(max, current));

                    Thread.sleep(500); // Имитация работы

                    concurrentCalls.decrementAndGet();
                    return "Success";
                }).get();
            } catch (Exception e) {
                // Игнорировать для теста
            }
        };

        // Запустить несколько задач одновременно
        List<CompletableFuture<Void>> futures = IntStream.range(0, 5)
            .mapToObj(i -> CompletableFuture.runAsync(task))
            .collect(Collectors.toList());

        futures.forEach(future -> {
            try {
                future.get(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                // Некоторые вызовы могут быть отклонены bulkhead
            }
        });

        // Максимум одновременных вызовов не должен превышать 2
        assertThat(maxConcurrent.get()).isLessThanOrEqualTo(2);
    }
}
```

### 5. Мониторинг в **production**

```java
// ✅ Хорошо - production-ready мониторинг
@Configuration
@Slf4j
public class ProductionMonitoringConfiguration {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry(MeterRegistry meterRegistry) {
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.ofDefaults();

        // Автоматическая регистрация метрик
        CircuitBreakerMetrics.ofCircuitBreakerRegistry(registry)
            .bindTo(meterRegistry);

        // Логирование состояний
        registry.getEventPublisher()
            .onStateTransition(event -> {
                CircuitBreaker circuitBreaker = event.getCircuitBreaker();
                CircuitBreaker.StateTransition transition = event.getStateTransition();

                log.warn("Circuit breaker {} transitioned from {} to {}",
                    circuitBreaker.getName(),
                    transition.getFromState(),
                    transition.getToState());
            });

        return registry;
    }

    @Bean
    public ResilienceHealthContributor resilienceHealthContributor(
            CircuitBreakerRegistry circuitBreakerRegistry,
            RetryRegistry retryRegistry,
            RateLimiterRegistry rateLimiterRegistry) {

        return new ResilienceHealthContributor(
            circuitBreakerRegistry,
            retryRegistry,
            rateLimiterRegistry
        );
    }

    // Кастомные алерты
    @Bean
    public AlertManager alertManager(CircuitBreakerRegistry circuitBreakerRegistry) {
        return new AlertManager(circuitBreakerRegistry);
    }
}

@Slf4j
@Component
public class AlertManager {

    public AlertManager(CircuitBreakerRegistry circuitBreakerRegistry) {
        circuitBreakerRegistry.getEventPublisher()
            .onStateTransition(event -> {
                CircuitBreaker circuitBreaker = event.getCircuitBreaker();

                if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
                    // Отправить алерт в систему мониторинга
                    sendAlert("Circuit breaker " + circuitBreaker.getName() + " is OPEN");
                }
            });
    }

    private void sendAlert(String message) {
        log.error("ALERT: {}", message);
        // Интеграция с PagerDuty, Slack, etc.
    }
}

// Health contributor для Spring Boot Actuator
public class ResilienceHealthContributor implements HealthContributor {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;

    public ResilienceHealthContributor(CircuitBreakerRegistry circuitBreakerRegistry,
                                    RetryRegistry retryRegistry,
                                    RateLimiterRegistry rateLimiterRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
    }

    @Override
    public Health health() {
        Health.Builder builder = Health.up();

        // Проверка circuit breakers
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(circuitBreaker -> {
            if (circuitBreaker.getState() == CircuitBreaker.State.OPEN) {
                builder.down()
                    .withDetail("circuitBreaker." + circuitBreaker.getName() + ".state", "OPEN")
                    .withDetail("circuitBreaker." + circuitBreaker.getName() + ".failureRate",
                        circuitBreaker.getMetrics().getFailureRate());
            }
        });

        return builder.build();
    }
}
```


## Заключение

**Resilience4j** — это мощная и легковесная библиотека для реализации **resilience** паттернов в **Java** приложениях. Она предоставляет высокопроизводительные реализации **Circuit Breaker**, **Retry**, **Rate Limiter**, **Bulkhead** и других паттернов.

### Преимущества **Resilience4j**

1. **Легковесность** — Минимальные зависимости, высокая производительность
2. **Функциональное программирование** — Использование **functional interfaces**
3. **Reactive streams** — Поддержка **reactive programming**
4. **Модульная архитектура** — Использование только нужных модулей
5. **Spring `Boot` интеграция** — Простая конфигурация через **properties**
6. **Micrometer метрики** — Интеграция с системами мониторинга
7. **Thread safety** — Все компоненты **thread-safe**
8. **Активное развитие** — Регулярные обновления и улучшения

### Основные паттерны использования

1. **Circuit `Breaker` паттерн** — Защита от каскадных сбоев
2. **Retry паттерн** — Повтор неудачных операций
3. **Rate `Limiter` паттерн** — Ограничение частоты запросов
4. **Bulkhead паттерн** — Изоляция компонентов
5. **Timeout паттерн** — Ограничение времени выполнения
6. **Fallback паттерн** — **Graceful degradation**

### Когда использовать **Resilience4j**

**Рекомендуется:**
- Микросервисная архитектура
- Приложения с внешними зависимостями
- Системы с высокими требованиями к надежности
- Приложения с переменной нагрузкой
- Проекты с **distributed** системами

**Особенно полезно:**
- Для защиты от сетевых проблем
- При работе с внешними **API**
- В системах с очередями сообщений
- При интеграции с базами данных
- В высоконагруженных приложениях

### Сравнение с альтернативами

| Библиотека | Преимущества | Недостатки |
|------------|-------------|------------|
| **Resilience4j** | Легковесная, функциональная, современная | Меньше **enterprise** фич |
| **Hystrix** | Зрелая, **Netflix battle-tested** | Устаревшая, тяжелая |
| **Sentinel** | **Alibaba**, **cloud-native** | Сложная конфигурация |
| **Failsafe** | Простая, легковесная | Меньше паттернов |

**Resilience4j** рекомендуется как основной выбор для современных **Java** приложений, требующих высокой надежности и **fault tolerance**.


[⬆️ Наверх](../)

## См. также

- [[java-apache-httpclient|Apache HttpClient: Мощный HTTP клиент для Java]]
- [[java-apache-poi|Apache POI]]
- [[java-bean-validation|Bean Validation (JSR-380 / Jakarta Validation 3.0)]]
- [[java-hikaricp|HikariCP: Высокопроизводительный Connection Pool]]
- [[java-http-clients|HTTP-клиенты в Java]]
