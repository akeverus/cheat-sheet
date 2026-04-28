---
title: "Micrometer"
description: "Micrometer — фасад (vendor-neutral API) для сбора метрик в приложениях на JVM. Поддерживает счётчики (Counter), таймеры (Timer), Gauge, распределения (DistributionSummary); экспорт в Prometheus, Graphite, InfluxDB, Datadog, StatsD и др. Интеграция с Spring Boot Actuator."
tags:
  - monitoring
  - metrics
  - micrometer
type: "reference"
difficulty: "intermediate"
aliases:
  - "Micrometer"
prerequisites: []
next:
  - "[[go-observability]]"
updated: "2026-04-20"
---
# Micrometer

Micrometer — фасад (vendor-neutral API) для сбора метрик в приложениях на JVM. Поддерживает счётчики (Counter), таймеры (Timer), Gauge, распределения (DistributionSummary); экспорт в Prometheus, Graphite, InfluxDB, Datadog, StatsD и др. Интеграция с Spring Boot Actuator.

## Полезные ссылки

### Официальная документация
- [Micrometer — Documentation](https://micrometer.io/docs)
- [Micrometer — Concepts](https://micrometer.io/docs/concepts)
- [Spring Boot — Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)

### Ресурсы
- [Baeldung — Micrometer](https://www.baeldung.com/micrometer)
- [Prometheus — Java client (Micrometer)](https://prometheus.io/docs/instrumenting/clientlibs/)

### См. также
- [Prometheus](prometheus.md) — сбор и хранение метрик
- [StatsD](statsd.md) — сбор метрик через StatsD
- [Infrastructure monitoring](../infrastructure-monitoring.md) — обзор мониторинга
- [Monitoring](../) — раздел мониторинга

- [Quarkus: Actuator — Health Checks и Metrics](../../frameworks/java-frameworks/quarkus/quarkus-actuator.md)
- [Micronaut: Actuator — Health Checks, Metrics и Endpoints](../../frameworks/java-frameworks/micronaut/micronaut-actuator.md)
- [Вопросы на собеседовании](../../interview/monitoring/micrometer-interview.md) — подготовка к интервью
## Содержание

- [Введение](#введение)
- [Установка и настройка](#установка-и-настройка)
  - [Maven (Prometheus)](#maven-prometheus)
  - [Gradle](#gradle)
  - [Базовое использование (без Spring)](#базовое-использование-без-spring)
- [Registry и Meter](#registry-и-meter)
- [Counter, Timer, Gauge, DistributionSummary](#counter-timer-gauge-distributionsummary)
- [Интеграция с Spring Boot](#интеграция-с-spring-boot)
- [Экспорт в Prometheus и другие бэкенды](#экспорт-в-prometheus-и-другие-бэкенды)
- [Теги и именование](#теги-и-именование)
- [Лучшие практики](#лучшие-практики)
- [Решение проблем](#решение-проблем)
- [Частые вопросы](#частые-вопросы)
- [Глоссарий](#глоссарий)
  - [Итоговые таблицы](#итоговые-таблицы)

## Введение

Micrometer — библиотека для сбора метрик в приложениях на Java/JVM. Предоставляет единый API (счётчики, таймеры, gauge, распределения) и адаптеры (Registry) для экспорта в Prometheus, Graphite, InfluxDB, Datadog, StatsD, JMX и др. Приложение инструментируется один раз; смена бэкенда — конфигурация Registry.

**Зачем использовать Micrometer:** единый API для разных бэкендов; интеграция с Spring Boot Actuator; типы метрик с семантикой и единицами; теги для фильтрации и группировки.


## Установка и настройка

### Maven (Prometheus)

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <version>1.12.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.micrometer:micrometer-registry-prometheus:1.12.0'
```

### Базовое использование (без Spring)

```java
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;

PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
Counter counter = registry.counter("my.counter", "tag", "value");
counter.increment();
Timer timer = registry.timer("my.timer");
timer.record(() -> { /* operation */ });
// Scrape: registry.scrape() возвращает текст в формате Prometheus
```


## Registry и Meter

MeterRegistry — точка регистрации метрик. Один Registry на бэкенд (например, PrometheusMeterRegistry). Meter — абстракция метрики (Counter, Timer, Gauge и т.д.). Имя и теги задаются при создании; один и тот же идентификатор (имя + теги) возвращает один и тот же Meter (кэш по Meter.Id).

CompositeMeterRegistry объединяет несколько дочерних регистров; при регистрации метрика попадает во все дочерние. Удобно для одновременного экспорта в Prometheus и StatsD.


## Counter, Timer, Gauge, DistributionSummary

| Тип | Назначение | Пример |
|-----|------------|--------|
| Counter | Монотонно растущий счётчик (запросы, ошибки) | registry.counter("http.requests", "status", "200").increment() |
| Timer | Длительность и частота событий (латентность, throughput) | timer.record(duration) или timer.record(() -> ...) |
| Gauge | Текущее значение (размер очереди, использование памяти) | registry.gauge("queue.size", queue, Queue::size) |
| DistributionSummary | Распределение величин (размер ответа, объём) | summary.record(amount) |

Timer в Prometheus даёт метрики с суффиксами _seconds, _count, _sum (и при необходимости max). Gauge не должен регистрироваться на объекты с коротким временем жизни без снятия регистрации (утечка и некорректные значения).

**Примеры кода:**

```java
// Counter и Timer
Counter requests = registry.counter("http.requests", "method", "GET", "uri", "/api/users");
requests.increment();
Timer timer = registry.timer("http.request.duration", "uri", "/api/users");
timer.record(Duration.ofMillis(150));
timer.record(() -> callExternalService());

// Gauge — привязан к объекту и функции
AtomicInteger queueSize = new AtomicInteger(0);
Gauge.builder("queue.size", queueSize, AtomicInteger::get)
    .tag("queue", "orders")
    .register(registry);

// DistributionSummary
DistributionSummary summary = registry.summary("http.response.size", "uri", "/api/data");
summary.record(1024);
```

LongTaskTimer — для длительных задач: учитывает текущее количество выполняющихся задач и их длительность. registry.more().longTaskTimer("task.name"); start() возвращает Sample, stop(sample) фиксирует длительность.


## Интеграция с Spring Boot

При наличии spring-boot-starter-actuator и micrometer-registry-prometheus метрики автоматически экспортируются на /actuator/prometheus. MeterRegistry внедряется как бин; можно регистрировать свои метрики. Автоконфигурация создаёт MeterBinder для JVM, Tomcat, Logback и др. при наличии зависимостей.

**application.yml (фрагмент):**

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, prometheus, metrics
  endpoint:
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: my-service
```

Собственный MeterBinder:

```java
@Component
public class CustomMetrics implements MeterBinder {
    @Override
    public void bindTo(MeterRegistry registry) {
        registry.gauge("custom.value", this, CustomMetrics::getValue);
    }
    private double getValue() { return 42.0; }
}
```

Свойства management.metrics.enable.* позволяют отключить отдельные метрики (например, jvm, process, tomcat).


## Экспорт в Prometheus и другие бэкенды

**Prometheus:** PrometheusMeterRegistry; Prometheus выполняет HTTP scrape эндпоинта (например, /actuator/prometheus). **Graphite:** GraphiteMeterRegistry (push в Graphite). **InfluxDB:** InfluxMeterRegistry. **StatsD:** StatsdMeterRegistry (push в StatsD). **JMX:** JmxMeterRegistry. Конфигурация через MeterRegistryCustomizer или свойства management.metrics.export.*.

| Registry | Бэкенд | Модель |
|----------|--------|--------|
| PrometheusMeterRegistry | Prometheus | Pull (scrape) |
| GraphiteMeterRegistry | Graphite | Push (step) |
| InfluxMeterRegistry | InfluxDB | Push (step) |
| StatsdMeterRegistry | StatsD | Push (step) |
| JmxMeterRegistry | JMX | Локальный MBean |
| SimpleMeterRegistry | In-memory | Тесты |


## Теги и именование

Рекомендуется naming convention: lowercase, точки для иерархии (http.server.requests). Теги — для размерности (status, method, uri). Избегать высокой кардинальности тегов (не использовать уникальный userId как тег), иначе рост числа series в Prometheus.

**Рекомендуется:** теги с ограниченным набором значений (status: 200, 404, 500; method: GET, POST; uri — группированный, например /api/users/{id}). **Антипаттерн:** тег с уникальным значением на каждый запрос (userId, requestId) — взрывной рост series.

MeterFilter позволяет добавлять общие теги, переименовывать, отключать метрики. Пример: MeterFilter.commonTags(Arrays.asList(Tag.of("env", "prod"), Tag.of("service", "my-app"))).


## Лучшие практики

1. Использовать общие имена метрик и теги для фильтрации; не создавать тысячи уникальных имён.
2. Timer для латентности операций; Counter для количества событий; Gauge для текущего состояния.
3. В Spring Boot добавлять теги приложения (management.metrics.tags.application) для группировки в Prometheus.
4. Не регистрировать Gauge на объекты с коротким временем жизни без снятия регистрации (registry.remove).
5. При использовании Prometheus учитывать кардинальность; ограничивать набор тегов для высокочастотных метрик.
6. Не называть метрику вручную с зарезервированными суффиксами Prometheus (_total, _count, _sum) — Micrometer сам добавляет их.


## Решение проблем

| Симптом | Возможная причина | Действие |
|---------|-------------------|----------|
| Метрики не появляются в Prometheus | Scrape не настроен или эндпоинт не открыт | Проверить management.endpoints.web.exposure.include (prometheus, metrics); проверить scrape_config в Prometheus |
| Высокий рост number of series в Prometheus | Слишком много уникальных тегов (например, по uri без группировки) | Ограничить теги; группировать uri (например, /api/{id}) в Spring MVC |
| Gauge показывает неверное значение | Объект для Gauge пересоздан или удалён | Регистрировать Gauge на стабильную ссылку; при необходимости registry.remove() |
| Метрики дублируются | Несколько Registry или несколько приложений с одним именем | Один Registry на бэкенд; уникальные теги приложения (instance, app) |
| Scrape возвращает 404 | Эндпоинт prometheus не открыт | management.endpoints.web.exposure.include=prometheus |
| IllegalArgumentException: Prometheus reserved suffix | Имя метрики заканчивается на _total, _count, _sum | Переименовать метрику; Micrometer сам добавляет суффиксы |
| Timeout при scrape | Слишком много метрик или медленный scrape() | Уменьшить число метрик; отключить ненужные MeterBinder; увеличить timeout в Prometheus |


## Частые вопросы

**Нужен ли Micrometer, если использую только Prometheus?** Micrometer не обязателен; можно использовать prometheus_client_java напрямую. Micrometer даёт единый API и удобную интеграцию с Spring Boot; при смене бэкенда код метрик не меняется.

**Как добавить кастомную метрику в Spring Boot?** Внедрить MeterRegistry и вызвать registry.counter(...), registry.timer(...) и т.д. Либо реализовать MeterBinder и зарегистрировать его как бин.

**Поддерживает ли Micrometer гистограммы?** Timer и DistributionSummary поддерживают percentiles и histogram (slots); настраивается при создании (Timer.Builder, DistributionSummary.Builder). В Prometheus при histogram создаются метрики с суффиксом _bucket.

**Как экспортировать в несколько бэкендов?** Зарегистрировать несколько MeterRegistry (например, PrometheusMeterRegistry и StatsdMeterRegistry) или использовать CompositeMeterRegistry и добавлять в него все нужные регистры; одна регистрация метрики попадёт во все дочерние.

**Как измерить время метода в Spring MVC?** Spring Boot автоматически регистрирует Timer для http.server.requests при наличии actuator и micrometer-registry-*. Для кастомного кода обернуть вызов в timer.record(Runnable) или timer.record(Callable).


## Глоссарий

| Термин | Описание |
|--------|----------|
| Meter | Абстракция метрики (Counter, Timer, Gauge и т.д.) |
| MeterRegistry | Реестр метрик; регистрация и экспорт в бэкенд |
| Counter | Счётчик (монотонно возрастает) |
| Timer | Таймер (длительность, частота) |
| Gauge | Текущее значение (может уменьшаться) |
| DistributionSummary | Распределение величин (без единицы времени) |
| Tag | Метка (ключ-значение) для размерности метрики |
| MeterBinder | Компонент, регистрирующий набор метрик в Registry |
| MeterFilter | Фильтр для изменения/отбрасывания метрик при регистрации |
| NamingConvention | Правило преобразования имён и тегов для бэкенда |
| Step | Интервал публикации для push-бэкендов |

### Итоговые таблицы

**Типы Meter (сводка):**

| Тип | Методы | Использование |
|-----|--------|---------------|
| Counter | increment(), increment(n) | Запросы, ошибки, сообщения |
| Timer | record(Duration), record(Callable), record(Runnable) | Латентность HTTP, DB, вызовов |
| Gauge | register(registry, name, obj, function) | Размер очереди, число соединений |
| DistributionSummary | record(value) | Размер тела запроса/ответа |
| LongTaskTimer | start(), stop(Sample) | Длительные задачи (in progress) |

**Зависимости Maven (выборка):** micrometer-core (ядро), micrometer-registry-prometheus, micrometer-registry-statsd, micrometer-registry-graphite, micrometer-registry-influx, micrometer-registry-jmx. В Spring Boot обычно достаточно micrometer-registry-prometheus; micrometer-core подтягивается транзитивно.

**Типы Meter и вывод в Prometheus:** Counter name_total; Timer name_seconds (histogram/summary), name_seconds_count, name_seconds_sum; Gauge name; DistributionSummary name_count, name_sum, name (histogram/summary); LongTaskTimer name_active_count, name_duration_seconds.


**Заключение.** Micrometer — фасад для метрик в JVM-приложениях с экспортом в Prometheus, Graphite, InfluxDB, StatsD и др. Используйте Counter, Timer, Gauge, DistributionSummary для инструментирования; в Spring Boot подключите micrometer-registry-prometheus и откройте /actuator/prometheus для Prometheus. Соблюдайте правила именования и ограничивайте кардинальность тегов. См. [Micrometer Docs](https://micrometer.io/docs), [Spring Boot Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics).
